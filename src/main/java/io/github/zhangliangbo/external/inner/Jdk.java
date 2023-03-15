package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangliangbo
 * @since 2023/3/12
 */
public class Jdk extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "jdk";
    }

    @Override
    public String getExecutable(String name) throws Exception {
        String directory = super.getExecutable(name);
        File root = new File(directory, "bin");
        return searchExecutable(root, name);
    }

    public Pair<Integer, String> runJar(String jar, String method) throws Exception {
        List<String> argList = new LinkedList<>();
        argList.add("-agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image");
        argList.add("-jar");
        argList.add(jar);
        argList.add(method);
        String[] newArgs = argList.toArray(new String[]{});
        return executeSub("java", newArgs);
    }

    public Pair<Integer, String> version() throws Exception {
        return executeSub("java", "-version");
    }

    public Pair<Integer, String> flags() throws Exception {
        return executeSub("java", "-XX:+PrintFlagsFinal", "-version");
    }

}
