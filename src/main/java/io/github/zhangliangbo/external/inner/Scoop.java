package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public class Scoop extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "scoop";
    }

    public String apps() throws Exception {
        Pair<Integer, String> pair = execute("list");
        return pair.getRight();
    }

    public String search(String app) throws Exception {
        Pair<Integer, String> pair = execute("search", app);
        return pair.getRight();
    }

    public String installApp(String app) throws Exception {
        int times = 0;
        while (true) {
            try {
                Pair<Integer, String> pair = execute("install", app);
                System.out.println(pair);
                return pair.getRight();
            } catch (Exception e) {
                System.out.printf("安装报错 开始重试 %s\n", ++times);
                TimeUnit.SECONDS.sleep(30);
            }
        }
    }

    public String installJdk() throws Exception {
        return installApp("graalvm22-jdk17");
    }

    public List<String> bucketAll() throws Exception {
        Pair<Integer, String> execute = execute("bucket", "known");
        if (execute.getLeft() == 0) {
            String[] split = execute.getRight().split("\n");
            return Stream.of(split).collect(Collectors.toList());
        }
        return new LinkedList<>();
    }

    public boolean bucketAdd(String bucket) throws Exception {
        int times = 10;
        while (times-- > 0) {
            System.out.printf("开始添加%s %s次", bucket, times);
            Pair<Integer, String> execute = execute("bucket", "add", bucket);
            boolean success = success(execute);
            if (success) {
                return true;
            }
        }
        return false;
    }

    public String bucketList() throws Exception {
        Pair<Integer, String> execute = execute("bucket", "list");
        return execute.getRight();
    }

    public Boolean update() throws Exception {
        Pair<Integer, String> execute = execute("update");
        return success(execute);
    }

    private boolean success(Pair<Integer, String> pair) {
        if (pair.getLeft() != 0) {
            return false;
        }
        return Stream.of("error", "fatal").noneMatch(t -> pair.getRight().toLowerCase().contains(t));
    }

}
