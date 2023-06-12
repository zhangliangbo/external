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

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("$PSVersionTable.PSVersion");
        return execute.getRight();
    }

}
