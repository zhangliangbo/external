package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.ET;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.Map;

/**
 * 外部程序
 *
 * @author zhangliangbo
 * @since 2023/1/1
 */
public interface ExternalExecutable {

    String getName();

    File getExecutableHome() throws Exception;

    String getExecutable() throws Exception;

    String getExecutable(String name) throws Exception;

    default Pair<Integer, String> execute(Map<String, String> env, String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(env, getExecutable(), directory, timeout, args);
    }

    default Pair<Integer, String> execute(String... args) throws Exception {
        return ET.exec.execute(null, getExecutable(), null, 0, args);
    }

    default Pair<Integer, String> executeSub(Map<String, String> env, String name, String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(env, getExecutable(name), directory, timeout, args);
    }

    default Pair<Integer, String> executeSub(String name, String... args) throws Exception {
        return ET.exec.execute(null, getExecutable(name), null, 0, args);
    }

    default Pair<Integer, String> executeRaw(Map<String, String> env, String name, String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(env, name, directory, timeout, args);
    }

    default Pair<Integer, String> executeRaw(String name, String... args) throws Exception {
        return ET.exec.execute(null, name, null, 0, args);
    }

}
