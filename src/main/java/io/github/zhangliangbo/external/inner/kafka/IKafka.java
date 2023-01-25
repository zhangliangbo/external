package io.github.zhangliangbo.external.inner.kafka;

import io.github.zhangliangbo.external.inner.ExternalExecutable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhangliangbo
 * @since 2023/1/24
 */
public interface IKafka extends ExternalExecutable {

    default String generateClusterID() throws Exception {
        Pair<Integer, String> execute = execute(null, "kafka-storage", null, 0, "random-uuid");
        return execute.getRight();
    }

    default String formatStorageDirectories(String clusterId, File configFile) throws Exception {
        Pair<Integer, String> execute = execute(null, "kafka-storage",
                null, 0,
                "format", "-t", clusterId, "-c", configFile.getAbsolutePath());
        return execute.getRight();
    }

    default boolean newServerPropertyFile(File[] files, int[] id, int[] brokerPort, int[] controllerPort, File[] logs) throws Exception {
        File source = new File(getExecutableFile(), "config/kraft/server.properties");
        for (int i = 0; i < files.length; i++) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(source));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(files[i]));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("#")) {
                    bufferedWriter.write(line);
                } else if (line.startsWith("node.id")) {
                    String s = line.replaceAll("(\\w+=).*", "$1" + id[i]);
                    bufferedWriter.write(s);
                } else if (line.startsWith("controller.quorum.voters")) {
                    List<String> list = new LinkedList<>();
                    for (int j = 0; j < id.length; j++) {
                        String one = id[j] + "@localhost:" + controllerPort[j];
                        list.add(one);
                    }
                    String value = String.join(",", list);
                    String s = line.replaceAll("(\\w+=).*", "$1" + value);
                    bufferedWriter.write(s);
                } else if (line.startsWith("listeners")) {
                    String value = String.format("PLAINTEXT://:%s,CONTROLLER://:%s", brokerPort[i], controllerPort[i]);
                    String s = line.replaceAll("(\\w+=).*", "$1" + value);
                    bufferedWriter.write(s);
                } else if (line.startsWith("advertised.listeners")) {
                    String value = String.format("PLAINTEXT://localhost:%s", brokerPort[i]);
                    String s = line.replaceAll("(\\w+=).*", "$1" + value);
                    bufferedWriter.write(s);
                } else if (line.startsWith("log.dirs")) {
                    if (!logs[i].exists()) {
                        if (!logs[i].mkdirs()) {
                            throw new Exception(String.format("创建目录失败%s", logs[i].getAbsolutePath()));
                        }
                    }
                    String value = StringEscapeUtils.escapeJava(logs[i].getAbsolutePath().replace("\\", "\\\\"));
                    String s = line.replaceAll("(\\w+=).*", "$1" + value);
                    bufferedWriter.write(s);
                } else {
                    bufferedWriter.write(line);
                }
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
            bufferedReader.close();
        }
        return true;
    }

    default Boolean changeProperty(File file, String key, String value) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, randomAccessFile.length());
        int equalSignPos = 0;
        int lineStart = -1;
        for (int i = 0; i < randomAccessFile.length(); i++) {
            byte b = mappedByteBuffer.get(i);
            if (b == '=') {
                equalSignPos = i;
            }
            if (b == '\n') {
                byte[] bytes = new byte[i - lineStart];
                mappedByteBuffer.get(bytes);
                String line = new String(bytes);
                if (line.startsWith(key)) {
                    byte[] update = value.getBytes();
                    int start = equalSignPos + 1;
                    for (int j = 0; j < update.length; j++) {
                        mappedByteBuffer.put(start + j, update[j]);
                    }
                    break;
                }
                lineStart = i;
            }
        }
        mappedByteBuffer.force();
        randomAccessFile.close();
        return Boolean.TRUE;
    }

    default void startOneNode(File file) throws Exception {
        execute(null, "kafka-server-start", null, -1, file.getAbsolutePath());
    }

    default boolean deployKRaft() throws Exception {
        File[] configs = new File[]{
                new File(getExecutableFile(), "config/kraft/server1.properties"),
                new File(getExecutableFile(), "config/kraft/server2.properties"),
                new File(getExecutableFile(), "config/kraft/server3.properties")
        };
        int[] id = new int[]{1, 2, 3};
        int[] brokerPort = new int[]{9092, 9093, 9094};
        int[] controllerPort = new int[]{8092, 8093, 8094};
        File[] logs = new File[]{
                new File(getExecutableFile(), "data/log1"),
                new File(getExecutableFile(), "data/log2"),
                new File(getExecutableFile(), "data/log3")
        };
        boolean res = newServerPropertyFile(configs, id, brokerPort, controllerPort, logs);
        if (!res) {
            throw new Exception("创建配置文件失败");
        }
        String clusterID = generateClusterID();
        System.out.println(clusterID);
        for (File file : configs) {
            System.out.println(formatStorageDirectories(clusterID, file));
        }
        List<CompletableFuture<Void>> list = new LinkedList<>();
        for (File config : configs) {
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                try {
                    startOneNode(config);
                } catch (Exception e) {
                    System.out.println("启动节点报错");
                }
            }).exceptionally(throwable -> {
                System.out.printf("启动节点报错%s\n", throwable);
                return null;
            });
            list.add(completableFuture);
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return true;
    }

    default String metadataQuorum() throws Exception {
        Pair<Integer, String> pair = execute(null, "kafka-metadata-quorum", "", 0,
                "--bootstrap-server", "localhost:9092,localhost:9093,localhost:9094",
                "describe", "--status");
        return pair.getRight();
    }

}
