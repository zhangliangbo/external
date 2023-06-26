package io.github.zhangliangbo.external.inner;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    /**
     * 单机部署时 修改堆大小为-Xmx256m -Xms256m -Xmn128m
     */
    public void startNameServer() throws Exception {
        executeSub(null, "mqnamesrv", null, -1);
    }

    /**
     * 单机部署时 修改堆大小为-Xmx256m -Xms256m -Xmn128m
     */
    public void startBroker() throws Exception {
        executeSub(null, "mqbroker", null, -1, "-n", "localhost:9876");
    }

    public void stopNameServer() throws Exception {
        executeSub("mqshutdown", "namesrv");
    }

    public void stopBroker() throws Exception {
        executeSub("mqshutdown", "broker");
    }

    public void startAfterStop() throws Exception {
        stop();
        System.out.println("睡眠5s 等待之前启动的NameServer Broker结束");
        TimeUnit.SECONDS.sleep(5);
        start();
    }

    public void start() throws Exception {
        CompletableFuture<Void> nameServerCf = CompletableFuture.runAsync(() -> {
            try {
                startNameServer();
            } catch (Exception e) {
                //ignore
            }
        });
        CompletableFuture<Void> brokerCf = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("睡眠10s 等待NameServer启动完毕");
                TimeUnit.SECONDS.sleep(10);
                startBroker();
            } catch (Exception e) {
                //ignore
            }
        });
        CompletableFuture.allOf(nameServerCf, brokerCf).join();
    }

    public void stop() throws Exception {
        stopBroker();
        stopNameServer();
    }

}
