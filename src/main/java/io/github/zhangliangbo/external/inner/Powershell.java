package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023/6/12
 */
public class Powershell extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "powershell";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new Powershell().commandSource("java"));
    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("$PSVersionTable.PSVersion");
        return execute.getRight();
    }

    /**
     * www.ipaddress.com
     * raw.githubusercontent.com
     */
    public String installScoop() throws Exception {
        Pair<Integer, String> execute = execute(
                "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser;" +
                        "[environment]::setEnvironmentVariable('SCOOP','D:\\scoop','User');" +
                        "irm get.scoop.sh | iex"
        );
        return execute.getRight();
    }

    public String bucket(String app) throws Exception {
        Pair<Integer, String> execute = execute(String.format("scoop info %s | Select -ExpandProperty Bucket", app));
        return execute.getRight();
    }

    public String commandSource(String cmd) throws Exception {
        Pair<Integer, String> execute = execute(String.format("Get-Command -Name %s -ErrorAction SilentlyContinue | Select -ExpandProperty Source", cmd));
        if (execute.getLeft() != 0) {
            return null;
        }
        return execute.getRight();
    }

    public String getEnv(String key) throws Exception {
        Pair<Integer, String> execute = execute(String.format("[Environment]::GetEnvironmentVariable('%s')", key));
        return execute.getRight();
    }

    public Boolean setEnv(String key, String value) throws Exception {
        Pair<Integer, String> execute = execute(String.format("[Environment]::SetEnvironmentVariable('%s','%s','User')", key, value));
        return execute.getLeft() == 0;
    }

}
