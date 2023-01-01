package io.github.zhangliangbo.external.inner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public abstract class AbstractExternalExecutable implements ExternalExecutable {
    @Override
    public String execute(String... args) throws Exception {
        CommandLine commandLine = new CommandLine(getExecutable());
        commandLine.addArguments(args);
        LogOutputStream log = new LogOutputStream() {

            @Override
            protected void processLine(String line, int logLevel) {
                System.out.println(line);
            }
        };
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler handler = new PumpStreamHandler(log);
        executor.setStreamHandler(handler);
        executor.execute(commandLine);
        executor.wait();
        return null;
    }
}
