package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

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

}
