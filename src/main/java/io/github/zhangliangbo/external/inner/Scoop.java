package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * https://www.modb.pro/db/499981
 *
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

    public String installApp(String bucket, String app) throws Exception {
        int times = 0;
        bucket = bucket.substring(0, 1).toUpperCase() + bucket.substring(1);
        String url = "https://ghproxy.com/https://raw.githubusercontent.com/ScoopInstaller/%s/master/bucket/%s.json";
        String path = String.format(url, bucket, app);
        while (true) {
            try {
                Pair<Integer, String> pair = execute("install", path);
                boolean success = success(pair);
                if (success) {
                    return pair.getRight();
                } else {
                    System.out.printf("%s安装报错 开始重试 %s\n", app, ++times);
                }
            } catch (Exception e) {
                System.out.printf("安装报错 开始重试 %s\n", ++times);
                TimeUnit.SECONDS.sleep(30);
            }
        }
    }

    public String installJdk() throws Exception {
        return installApp("Main", "graalvm22-jdk17");
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
            System.out.printf("开始添加%s %s\n", bucket, times);
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

    public Boolean update(String app) throws Exception {
        Pair<Integer, String> execute = execute("update", app);
        return success(execute);
    }

    private boolean success(Pair<Integer, String> pair) {
        if (pair.getLeft() != 0) {
            return false;
        }
        return Stream.of("error", "fatal").noneMatch(t -> pair.getRight().toLowerCase().contains(t));
    }

    public boolean config(String config, String value) throws Exception {
        Pair<Integer, String> execute = execute("config", config, value);
        return success(execute);
    }

    /**
     * https://ghproxy.com/
     * https://toolwa.com/github/
     * 默认 https://ghproxy.com/
     */
    public boolean proxy() throws Exception {
        return config("proxy", "https://ghproxy.com/");
    }

    public String config(String config) throws Exception {
        Pair<Integer, String> execute = execute("config", config);
        return execute.getRight();
    }

    public boolean changeScoopRepo() throws Exception {
        String key = "scoop_repo";
        String value = config(key);
        Pair<Integer, String> execute = execute("config", key, "https://ghproxy.com/" + value);
        return success(execute);
    }

}
