package io.github.zhangliangbo.external.inner;

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
    public Pair<Integer, String> execute(String directory, String... args) throws Exception {
        CommandLine commandLine = new CommandLine(getExecutable());
        commandLine.addArguments(args);
        StringBuilder stringBuilder = new StringBuilder();
        DefaultExecutor executor = new DefaultExecutor();
        LogOutputStream log = new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                stringBuilder.append(line).append("\n");
            }
        };
        PumpStreamHandler handler = new PumpStreamHandler(log);
        executor.setStreamHandler(handler);
        if (StringUtils.isNotBlank(directory)) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                executor.setWorkingDirectory(file);
            }
        }
        int exitCode = executor.execute(commandLine);
        String result = stringBuilder.toString();
        return Pair.of(exitCode, result);
    }
}
