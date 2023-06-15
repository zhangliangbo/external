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
        Pair<Integer, String> execute = execute("remove", app);
        return execute.getLeft() == 0;
    }


    public Boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", "notebook=6.4.12");
        return execute.getLeft() == 0;
    }

}
