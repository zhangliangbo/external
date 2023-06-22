package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FileUtils;
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
public class RocketMq extends AbstractExternalExecutable {

    public static final String HOME_KEY = "ROCKETMQ_HOME";

    @Override
    public String getName() {
        return "rocketmq";
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell) throws Exception {
        if (OsType.infer() == OsType.Unix) {
            File file = Environment.searchLocal(getName());
            if (Objects.nonNull(file)) {
                return file.getAbsolutePath();
            }
        }
        return powershell.getEnv(RocketMq.HOME_KEY);
    }

    @Override
    public String getExecutable(String name) throws Exception {
        String directory = super.getExecutable(name);
        File root = new File(directory, "bin");
        return searchExecutable(root, name);
    }

    public void startNameServer() throws Exception {
        executeSub(null, "mqnamesrv", null, -1);
    }


}
