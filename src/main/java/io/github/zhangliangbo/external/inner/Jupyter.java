package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023/6/12
 */
public class Jupyter extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "jupyter";
    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("--version");
        return execute.getRight();
    }

    public Boolean applyExtension() throws Exception {
        Pair<Integer, String> execute = execute("contrib", "nbextension", "install", "--user");
        return execute.getLeft() == 0;
    }

    public Boolean enableExtension() throws Exception {
        Pair<Integer, String> execute = execute("nbextension", "enable", "Hinterland/main", "--user");
        return execute.getLeft() == 0;
    }

}
