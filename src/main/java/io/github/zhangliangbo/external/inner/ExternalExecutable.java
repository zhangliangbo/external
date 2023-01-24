package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

/**
 * 外部程序
 *
 * @author zhangliangbo
 * @since 2023/1/1
 */
public interface ExternalExecutable {

    String getName();

    File getExecutableFile() throws Exception;

    String getExecutable() throws Exception;

    String getExecutable(String name) throws Exception;

    void setExecutableFactory(Function<String, File> function);

    Function<String, File> getExecutableFactory();

    Pair<Integer, String> execute(Map<String, String> env, String directory, long timeout, String... args) throws Exception;

    Pair<Integer, String> execute(Map<String, String> env, String name, String directory, long timeout, String... args) throws Exception;
}
