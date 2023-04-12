package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

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

}
