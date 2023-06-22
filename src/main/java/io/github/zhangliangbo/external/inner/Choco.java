package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * @author zhangliangbo
 * @since 2023-06-19
 */
public class Choco extends AbstractExternalExecutable {

    public static final String APP_DIR = "D:\\chocolatey";

    @Override
    public String getName() {
        return "choco";
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell) throws Exception {
        String autoDetect = super.autoDetect(cmd, powershell);
        File file = new File(Choco.APP_DIR);
        if (file.mkdirs()) {
            Boolean env = powershell.setEnv("ChocolateyToolsLocation", Choco.APP_DIR);
            System.out.printf("设置Chocolatey程序安装目录%s %s\n", Choco.APP_DIR, env);
        }
        return autoDetect;
    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("version");
        return execute.getRight();
    }

    public boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", app);
        return execute.getLeft() == 0;
    }

}
