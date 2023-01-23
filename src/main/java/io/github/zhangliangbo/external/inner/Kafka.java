package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author zhangliangbo
 * @since 2023/1/22
 */
public class Kafka extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "kafka";
    }

    @Override
    public String getExecutable(String name) throws Exception {
        OsType infer = OsType.infer();
        String directory = super.getExecutable(name);
        if (infer == OsType.Windows) {
            File win = new File(directory, "bin/windows");
            File[] files = win.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = FilenameUtils.getName(file.getAbsolutePath());
                    if (fileName.startsWith(name)) {
                        return file.getAbsolutePath();
                    }
                }
            }
            throw new Exception(name + "未找到");
        } else {
            return directory;
        }
    }

    public String generateClusterID() throws Exception {
        Pair<Integer, String> execute = execute(null, "kafka-storage", null, 0, "random-uuid");
        return execute.getRight();
    }

    public String formatStorageDirectories(String id) throws Exception {
        Pair<Integer, String> execute = execute(null, "kafka-storage",
                null, 0,
                "format", "-t", id, "-c", "./config/kraft/server.properties");
        return execute.getRight();
    }

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
                    for (int j = 0; j < update.length; j++) {
                        mappedByteBuffer.put(equalSignPos + 1 + j, update[j]);
                    }
                    mappedByteBuffer.force();
                    break;
                }
                lineStart = i;
            }
        }
        randomAccessFile.close();
        return Boolean.TRUE;
    }

}
