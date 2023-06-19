package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023-06-19
 */
public class Choco extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "choco";
    }

    public boolean install(String app) throws Exception {
        Pair<Integer, String> execute = execute("install", app);
        return execute.getLeft() == 0;
    }

}
