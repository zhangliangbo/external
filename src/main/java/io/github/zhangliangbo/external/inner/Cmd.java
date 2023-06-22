package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangliangbo
 * @since 2023/4/12
 */
public class Cmd extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "cmd";
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell) throws Exception {
        return System.getenv("SystemRoot") + File.separator + "System32" + File.separator + "cmd.exe";
    }

    public String echo(String variable) throws Exception {
        Pair<Integer, String> pair = execute("/c", "echo", "%" + variable + "%");
        if (pair.getLeft() != 0) {
            return null;
        }
        return pair.getRight();
    }

    public List<String> where(String exe) throws Exception {
        Pair<Integer, String> pair = execute("/c", "where", exe);
        if (pair.getLeft() != 0) {
            return null;
        }
        String right = pair.getRight();
        String[] split = right.split("\n");
        return Stream.of(split).collect(Collectors.toList());
    }

    public void restart(int delay) throws Exception {
        execute("/c", "shutdown", "-r", "-t", String.valueOf(delay));
    }

    public void restart() throws Exception {
        restart(15);
    }

}
