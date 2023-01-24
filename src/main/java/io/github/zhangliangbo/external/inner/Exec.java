package io.github.zhangliangbo.external.inner;

import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public class Exec {

    public Pair<Integer, String> execute(Map<String, String> env, String executable, String directory, long timeout, String... args) throws Exception {
        CommandLine commandLine = new CommandLine(executable);
        return execute(env, commandLine, directory, timeout, args);
    }

    private Pair<Integer, String> execute(Map<String, String> env, CommandLine commandLine, String directory, long timeout, String... args) throws IOException {
        commandLine.addArguments(args);
        System.out.println(commandLine);
        StringBuilder stringBuilder = new StringBuilder();
        DefaultExecutor executor = new DefaultExecutor();
        LogOutputStream log = new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                if (timeout == -1) {
                    //长时间运行任务，打印日志
                    System.out.println(line);
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(line);
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
        executor.setExitValues(null);
        int exitCode = executor.execute(commandLine, env);
        String result = stringBuilder.toString();
        return Pair.of(exitCode, result);
    }

}
