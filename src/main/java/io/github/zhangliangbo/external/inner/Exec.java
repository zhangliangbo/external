package io.github.zhangliangbo.external.inner;

import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public class Exec {
    public Pair<Integer, String> execute(ExternalExecutable executable, String directory, long timeout, String... args) throws IOException {
        CommandLine commandLine = new CommandLine(executable.getExecutable());
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
        if (timeout > 0) {
            ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(timeout);
            executor.setWatchdog(executeWatchdog);
        }
        int exitCode = executor.execute(commandLine);
        String result = stringBuilder.toString();
        return Pair.of(exitCode, result);
    }
}
