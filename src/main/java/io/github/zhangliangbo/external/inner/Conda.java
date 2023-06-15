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

    public static void main(String[] args) {

    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("--version");
        return execute.getRight();
    }

    public String apps() throws Exception {
        Pair<Integer, String> execute = execute("list");
        System.out.println(execute);
        return execute.getRight();
    }

    public Boolean remove(String app) throws Exception {
        Pair<Integer, String> execute = execute("remove", "-q", "-y", app);
        return execute.getLeft() == 0 || notFound(execute.getRight());
    }

    private Boolean notFound(String content) {
        return content.contains("PackagesNotFoundError");
    }

    public Boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", app);
        return execute.getLeft() == 0;
    }

}
