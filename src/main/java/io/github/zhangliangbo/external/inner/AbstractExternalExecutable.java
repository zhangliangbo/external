package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.ET;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public abstract class AbstractExternalExecutable implements ExternalExecutable {
    @Override
    public Pair<Integer, String> execute(String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(this, directory, timeout, args);
    }

    @Override
    public Pair<Integer, String> execute(long timeout, String... args) throws Exception {
        return ET.exec.execute(this, null, timeout, args);
    }

    @Override
    public Pair<Integer, String> execute(String... args) throws Exception {
        return ET.exec.execute(this, null, 0, args);
    }
}
