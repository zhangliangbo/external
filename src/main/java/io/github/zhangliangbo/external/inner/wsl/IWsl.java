package io.github.zhangliangbo.external.inner.wsl;

import io.github.zhangliangbo.external.ET;
import io.github.zhangliangbo.external.inner.ExternalExecutable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author zhangliangbo
 * @since 2023/1/24
 */
public interface IWsl extends ExternalExecutable {

    default Pair<Integer, String> executeInWsl(Map<String, String> env, String executale, String directory, long timeout, String... args) throws Exception {
        String[] newArgs = ArrayUtils.addFirst(args, executale);
        return ET.exec.execute(env, getExecutable(), directory, timeout, newArgs);
    }

}
