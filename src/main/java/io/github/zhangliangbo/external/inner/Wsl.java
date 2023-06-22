package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.ET;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author zhangliangbo
 * @since 2023/1/24
 */
public class Wsl extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "wsl";
    }

    public String update() throws Exception {
        Pair<Integer, String> execute = execute("--update");
        return execute.getRight();
    }

    public Pair<Integer, String> executeInWsl(Map<String, String> env, String executable, String directory, long timeout, String... args) throws Exception {
        String[] newArgs = ArrayUtils.addFirst(args, executable);
        return ET.exec.execute(env, getExecutable(), directory, timeout, newArgs);
    }

    public Pair<Integer, String> executeInWsl(String executable, String... args) throws Exception {
        String[] newArgs = ArrayUtils.addFirst(args, executable);
        return ET.exec.execute(null, getExecutable(), SystemUtils.getUserDir().getAbsolutePath(), 0, newArgs);
    }

}
