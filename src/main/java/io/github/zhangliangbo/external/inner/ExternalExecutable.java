package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

/**
 * 外部程序
 *
 * @author zhangliangbo
 * @since 2023/1/1
 */
public interface ExternalExecutable {

    String getName();

    String getExecutable() throws Exception;

    void setExecutableFactory(Function<String, String> function);

    Function<String, String> getExecutableFactory();

    Pair<Integer, String> execute(String directory, long timeout, String... args) throws Exception;

    Pair<Integer, String> execute(long timeout, String... args) throws Exception;

    Pair<Integer, String> execute(String... args) throws Exception;
}
