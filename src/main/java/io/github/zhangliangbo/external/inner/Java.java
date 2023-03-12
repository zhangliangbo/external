package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangliangbo
 * @since 2023/3/12
 */
public class Java extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "java";
    }

    public Pair<Integer, String> runJar(String jar, String method) throws Exception {
        List<String> argList = new LinkedList<>();
        argList.add("-agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image");
        argList.add("-jar");
        argList.add(jar);
        argList.add(method);
        String[] newArgs = argList.toArray(new String[]{});
        return execute(newArgs);
    }

    public Pair<Integer, String> version() throws Exception {
        return execute("-version");
    }

}
