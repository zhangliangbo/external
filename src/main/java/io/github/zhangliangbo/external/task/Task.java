package io.github.zhangliangbo.external.task;

import io.github.zhangliangbo.external.ET;
import io.github.zhangliangbo.external.inner.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangliangbo
 * @since 2023/6/14
 */
public class Task {
    /**
     * 部署新的机器
     */
    public void deployNewMachine() throws Exception {
        String result;
        List<String> scoop = ET.cmd.where("scoop");
        if (CollectionUtils.isNotEmpty(scoop)) {
            System.out.printf("%s已安装，跳过\n", "scoop");
        } else {
            result = ET.powershell.installScoop();
            System.out.println(result);
        }
        //修改repo
        boolean b = ET.scoop.changeScoopRepo();
        System.out.printf("更新%s结果%s\n", "repo", b);
        //安装git
        List<String> git = ET.cmd.where("git");
        if (CollectionUtils.isNotEmpty(git)) {
            System.out.printf("%s已安装，跳过\n", "git");
        } else {
            String bucket = ET.powershell.bucket("git");
            result = ET.scoop.installApp(bucket, "git");
            System.out.println(result);
        }
        //添加仓库
        b = ET.scoop.bucketAdd("extras");
        System.out.printf("添加%s结果%s\n", "extras", b);
        //安装anaconda3
        List<String> conda = ET.cmd.where("conda");
        if (CollectionUtils.isNotEmpty(conda)) {
            System.out.printf("%s已安装，跳过\n", "anaconda3");
        } else {
            String bucket = ET.powershell.bucket("anaconda3");
            result = ET.scoop.installApp(bucket, "anaconda3");
            System.out.println(result);
        }
        //安装notebook
        String cmd = ET.powershell.commandSource("conda");
        if (StringUtils.isNotBlank(cmd)) {
            System.out.printf("%s已安装，跳过\n", "conda");
            Environment.setExecutable("conda", cmd);

            b = ET.conda.remove("jinja2");
            System.out.printf("移除%s结果%s\n", "jinja2", b);
            b = ET.conda.install("jinja2=3.0.3");
            System.out.printf("安装%s结果%s\n", "jinja2=3.0.3", b);

            b = ET.conda.remove("notebook");
            System.out.printf("移除%s结果%s\n", "notebook", b);
            b = ET.conda.install("notebook=6.4.12");
            System.out.printf("安装%s结果%s\n", "notebook=6.4.12", b);

            b = ET.conda.remove("kotlin-jupyter-kernel");
            System.out.printf("移除%s结果%s\n", "kotlin-jupyter-kernel", b);
            b = ET.conda.installKotlinJupyterKernel();
            System.out.printf("安装%s结果%s\n", "kotlin-jupyter-kernel", b);

            b = ET.conda.remove("jupyter_contrib_nbextensions");
            System.out.printf("移除%s结果%s\n", "jupyter_contrib_nbextensions", b);
            b = ET.conda.installNbExtensions();
            System.out.printf("安装%s结果%s\n", "jupyter_contrib_nbextensions", b);

            cmd = ET.powershell.commandSource("jupyter");
            if (StringUtils.isNotBlank(cmd)) {
                System.out.printf("%s已安装，跳过\n", "jupyter");
                Environment.setExecutable("jupyter", cmd);

                b = ET.jupyter.applyExtension();
                System.out.printf("应用拓展结果%s\n", b);
                b = ET.jupyter.enableExtension();
                System.out.printf("启用拓展结果%s\n", b);
            }
        } else {
            System.out.println("命令为空");
        }
    }

    public void installJdk(String url) throws Exception {
        File file = ET.http.download(url);
        System.out.printf("文件地址%s\n", file.getAbsolutePath());

        String rootName = ET.io.rootName(file.getAbsolutePath());
        File local = Environment.searchLocal(rootName);
        if (Objects.isNull(local)) {
            try {
                Pair<Duration, File> pair = ET.io.extract(file.getAbsolutePath(), Environment.getHome().getAbsolutePath());
                System.out.printf("解压时间 %s %s\n", pair.getLeft(), pair.getRight());

                local = pair.getRight();
            } catch (Exception e) {
                //ignore
            }
        } else {
            System.out.printf("本地文件已解压 %s\n", local.getAbsolutePath());
        }

        if (Objects.isNull(local)) {
            return;
        }

        String env = Jdk.HOME_KEY;
        Boolean res = ET.powershell.setEnv(env, local.getAbsolutePath());
        System.out.printf("设置环境变量%s %s\n", env, res);
        addPath("%" + Jdk.HOME_KEY + "%" + File.separator + "bin");
    }

    /**
     * 安装graalvm17社区版本
     */
    public void installGraalVmJdkCe() throws Exception {
        installJdk("https://ghproxy.com/https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.3.2/graalvm-ce-java17-windows-amd64-22.3.2.zip");
    }

    /**
     * 安装openjdk8版本
     */
    public void installOpenJdk8() throws Exception {
        installJdk("https://download.java.net/openjdk/jdk8u43/ri/openjdk-8u43-windows-i586.zip");
    }

    private void addPath(String newPath) throws Exception {
        String env = "PATH";
        String path = ET.powershell.getEnv(env);
        if (StringUtils.isBlank(path)) {
            return;
        }
        String[] split = path.split(";");
        Set<String> set = Arrays.stream(split).collect(Collectors.toSet());
        set.add(newPath);
        path = String.join(";", set);
        Boolean res = ET.powershell.setEnv(env, path);
        System.out.printf("设置环境变量%s %s\n", env, res);
        if (OsType.infer() == OsType.Windows) {
            ET.cmd.restart();
        }
    }

    /**
     * 安装kafka
     */
    public void installKafka() throws Exception {
        File file = ET.http.download("https://downloads.apache.org/kafka/3.5.0/kafka_2.13-3.5.0.tgz");
        Pair<Duration, File> pair = ET.io.extract(file.getAbsolutePath(), Environment.getHome().getAbsolutePath());
        System.out.printf("解压时间%s\n", pair.getLeft());
        System.out.printf("解压地址%s\n", pair.getRight());

        if (OsType.infer() == OsType.Windows) {
            String env = Kafka.HOME_KEY;
            Boolean res = ET.powershell.setEnv(env, pair.getRight().getAbsolutePath());
            System.out.printf("设置环境变量%s %s\n", env, res);
            ET.cmd.restart();
        }
    }

    /**
     * 安装rocketmq
     */
    public void installRocketMq() throws Exception {
        File file = ET.http.download("https://dist.apache.org/repos/dist/release/rocketmq/5.1.2/rocketmq-all-5.1.2-bin-release.zip");
        Pair<Duration, File> pair = ET.io.extract(file.getAbsolutePath(), Environment.getHome().getAbsolutePath());
        System.out.printf("解压时间%s\n", pair.getLeft());
        System.out.printf("解压地址%s\n", pair.getRight());

        if (OsType.infer() == OsType.Windows) {
            String env = RocketMq.HOME_KEY;
            Boolean res = ET.powershell.setEnv(env, pair.getRight().getAbsolutePath());
            System.out.printf("设置环境变量%s %s\n", env, res);
            ET.cmd.restart();
        }
    }

}
