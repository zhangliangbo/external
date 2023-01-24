package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.inner.kafka.IKafka;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author zhangliangbo
 * @since 2023/1/22
 */
public class Kafka extends AbstractExternalExecutable implements IKafka {

    @Override
    public String getName() {
        return "kafka";
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
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = FilenameUtils.getName(file.getAbsolutePath());
                if (fileName.startsWith(name)) {
                    return file.getAbsolutePath();
                }
            }
        }
        throw new Exception(String.format("%s %s未找到", infer.getCode(), name));
    }

}
