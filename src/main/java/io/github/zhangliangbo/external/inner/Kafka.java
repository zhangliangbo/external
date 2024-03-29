package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhangliangbo
 * @since 2023/1/22
 */
public class Kafka extends AbstractExternalExecutable {

    public static final String HOME_KEY = "KAFKA_HOME";

    @Override
    public String getName() {
        return "kafka";
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell) throws Exception {
        if (OsType.infer() == OsType.Unix) {
            File file = Environment.searchLocal(getName());
            if (Objects.nonNull(file)) {
                return file.getAbsolutePath();
            }
        }
        return powershell.getEnv(Kafka.HOME_KEY);
    }

    @Override
    public String getExecutable(String name) throws Exception {
        OsType infer = OsType.infer();
        String directory = super.getExecutable(name);
        File root;
        if (infer == OsType.Windows) {
            root = new File(directory, "bin/windows");
        } else {
            root = new File(directory, "bin");
        }
        return searchExecutable(root, name);
    }

    /**
     * 生成集群id
     */
    public String generateClusterID() throws Exception {
        Pair<Integer, String> execute = executeSub("kafka-storage", "random-uuid");
        return execute.getRight();
    }

    /**
     * 格式化存储目录
     */
    public String formatStorageDirectories(String clusterId, File configFile) throws Exception {
        Pair<Integer, String> execute = executeSub("kafka-storage", "format", "-t", clusterId, "-c", configFile.getAbsolutePath(), "--ignore-formatted");
        return execute.getRight();
    }

    /**
     * 根据配置文件母版新建一个配置文件
     */
    public boolean newServerPropertyFile(File[] files, int[] id, int[] brokerPort, int[] controllerPort, File[] logs) throws Exception {
        File source = new File(getExecutableHome(), "config/kraft/server.properties");
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

    /**
     * 内存映射文件技术修改文件
     */
    public Boolean changeProperty(File file, String key, String value) throws IOException {
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

    /**
     * 根据配置文件启动一个节点
     */
    public void startOneNode(File file) throws Exception {
        executeSub(null, "kafka-server-start", null, -1, file.getAbsolutePath());
    }

    /**
     * 单机部署kraft集群
     * window无法启动
     */
    public boolean deployKRaft() throws Exception {
        File[] configs = new File[]{
                new File(getExecutableHome(), "config/kraft/server1.properties"),
                new File(getExecutableHome(), "config/kraft/server2.properties"),
                new File(getExecutableHome(), "config/kraft/server3.properties")
        };
        int[] id = new int[]{1, 2, 3};
        int[] brokerPort = new int[]{9092, 9093, 9094};
        int[] controllerPort = new int[]{8092, 8093, 8094};
        File[] logs = new File[]{
                new File(getExecutableHome(), "data/log1"),
                new File(getExecutableHome(), "data/log2"),
                new File(getExecutableHome(), "data/log3")
        };
        //删除旧的数据文件，不然启动失败
        for (File log : logs) {
            System.out.printf("删除数据目录 %s\n", log.getAbsolutePath());
            FileUtils.forceDelete(log);
        }
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

    public String servers() {
        return "localhost:9092,localhost:9093,localhost:9094";
    }

    public String metadataQuorum() throws Exception {
        Pair<Integer, String> pair = executeSub("kafka-metadata-quorum", "describe", "--status");
        return pair.getRight();
    }

    public String topics() throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-topics", "", 0,
                "--bootstrap-server", servers(), "--list");
        return pair.getRight();
    }

    public String topicCreate(String name, String partitions, String replication) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-topics", "", 0,
                "--bootstrap-server", servers(), "--create",
                "--topic", name, "--partitions", partitions, "--replication-factor", replication);
        return pair.getRight();
    }

    public String topicConfig(String name) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-configs", "", 0,
                "--bootstrap-server", servers(), "--entity-type", "topics",
                "--entity-name", name, "--describe");
        return pair.getRight();
    }

    public String topicDelete(String name) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-topics", "", 0,
                "--bootstrap-server", servers(), "--delete",
                "--topic", name);
        return pair.getRight();
    }

    public String topicInfo(String name) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-topics", "", 0,
                "--bootstrap-server", servers(), "--topic", name, "--describe");
        return pair.getRight();
    }

    public String producerHelp() throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-console-producer", "", -1,
                "--bootstrap-server", servers(), "--help");
        return pair.getRight();
    }

    public String producer(String name) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-console-producer", "", -1,
                "--bootstrap-server", servers(), "--topic", name);
        return pair.getRight();
    }

    public String consumerHelp() throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-console-consumer", "", 0,
                "--bootstrap-server", servers(), "--help");
        return pair.getRight();
    }

    public String consumer(String name, String partition, String offset) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-console-consumer", "", -1,
                "--bootstrap-server", servers(),
                "--topic", name,
                "--partition", partition,
                "--offset", offset);
        return pair.getRight();
    }

    public String consumer(String name) throws Exception {
        Pair<Integer, String> pair = executeSub(null, "kafka-console-consumer", "", -1,
                "--bootstrap-server", servers(),
                "--topic", name,
                "--from-beginning");
        return pair.getRight();
    }

    public String connectStandalone() throws Exception {
        File file = new File(getExecutableHome(), "config/connect-standalone.properties");
        File source = new File(getExecutableHome(), "config/connect-file-source.properties");
        File sink = new File(getExecutableHome(), "config/connect-file-sink.properties");
        Pair<Integer, String> pair = executeSub(null, "connect-standalone", "", 0,
                file.getAbsolutePath(), source.getAbsolutePath(), sink.getAbsolutePath());
        return pair.getRight();
    }

}
