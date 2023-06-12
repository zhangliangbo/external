package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.TimeUnit;

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
                Pair<Integer, String> pair = execute(null, null, 0, "install", app);
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

}
