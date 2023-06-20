package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

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

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("version");
        return execute.getRight();
    }

    public boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", app);
        return execute.getLeft() == 0;
    }

}
