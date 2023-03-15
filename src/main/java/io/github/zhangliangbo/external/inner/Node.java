package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * @author zhangliangbo
 * @since 2023-03-15
 */
public class Node extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "node";
    }

    @Override
    public String getExecutable(String name) throws Exception {
        String directory = super.getExecutable(name);
        File root = new File(directory);
        return searchExecutable(root, name);
    }

    public Pair<Integer, String> listConfig() throws Exception {
        return executeSub("npm", "config", "list");
    }

    public Pair<Integer, String> setGlobalDir() throws Exception {
        File home = Environment.getHome();
        File myHome = new File(home, getName());
        File cache = new File(myHome, "cache");
        File prefix = new File(myHome, "prefix");
        FileUtils.forceMkdir(cache);
        FileUtils.forceMkdir(prefix);
        Pair<Integer, String> pair = executeSub("npm", "config", "set", "cache", cache.getAbsolutePath());
        System.out.println(pair);
        pair = executeSub("npm", "config", "set", "prefix", prefix.getAbsolutePath());
        System.out.println(pair);
        return listConfig();
    }

    public Pair<Integer, String> installCNPM() throws Exception {
        return executeSub("npm", "install", "-g", "cnpm", "--registry=https://registry.npmmirror.com");
    }


}
