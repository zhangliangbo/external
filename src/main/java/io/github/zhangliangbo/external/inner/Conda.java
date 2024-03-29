package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023/6/12
 */
public class Conda extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "conda";
    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("--version");
        return execute.getRight();
    }

    public String apps() throws Exception {
        Pair<Integer, String> execute = execute("list");
        return execute.getRight();
    }

    public Boolean remove(String app) throws Exception {
        Pair<Integer, String> execute = execute("remove", "-q", "-y", app);
        return notFound(execute.getRight()) || execute.getLeft() == 0;
    }

    private Boolean notFound(String content) {
        return content.contains("PackagesNotFoundError");
    }

    public Boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", "-q", "-y", app);
        return execute.getLeft() == 0;
    }

    public Boolean installKotlinJupyterKernel() throws Exception {
        Pair<Integer, String> execute = execute("install", "-q", "-y", "-c", "jetbrains", "kotlin-jupyter-kernel");
        return execute.getLeft() == 0;
    }

    public Boolean installNbExtensions() throws Exception {
        Pair<Integer, String> execute = execute("install", "-q", "-y", "-c", "conda-forge", "jupyter_contrib_nbextensions");
        return execute.getLeft() == 0;
    }

}
