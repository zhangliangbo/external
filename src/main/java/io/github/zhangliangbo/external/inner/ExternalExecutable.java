package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 外部程序
 *
 * @author zhangliangbo
 * @since 2023/1/1
 */
public interface ExternalExecutable {
    String getExecutable();

    Pair<Integer, String> execute(String directory, String... args) throws Exception;
}
