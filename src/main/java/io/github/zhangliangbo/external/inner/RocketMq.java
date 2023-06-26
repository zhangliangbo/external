package io.github.zhangliangbo.external.inner;

import java.io.File;
import java.util.Objects;

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

    public void startBroker() throws Exception {
        executeSub(null, "mqbroker", null, -1, "-n", "localhost:9876");
    }

    public void stopNameServer() throws Exception {
        executeSub("mqshutdown", "namesrv");
    }

    public void stopBroker() throws Exception {
        executeSub("mqshutdown", "broker");
    }

    public void stop() throws Exception {
        stopBroker();
        stopNameServer();
    }

}
