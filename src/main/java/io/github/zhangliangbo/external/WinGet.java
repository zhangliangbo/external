package io.github.zhangliangbo.external;

import io.github.zhangliangbo.external.inner.AbstractExternalExecutable;
import io.github.zhangliangbo.external.inner.Powershell;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangliangbo
 * @since 2023/4/12
 */
public class WinGet extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "winget";
    }

    public String version() throws Exception {
        Pair<Integer, String> execute = execute("-v");
        return execute.getRight();
    }

    public String search(String app) throws Exception {
        Pair<Integer, String> execute = execute("search", app);
        return execute.getRight();
    }

}
