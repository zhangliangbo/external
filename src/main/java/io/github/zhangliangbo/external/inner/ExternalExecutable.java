package io.github.zhangliangbo.external.inner;

/**
 * 外部程序
 *
 * @author zhangliangbo
 * @since 2023/1/1
 */
public interface ExternalExecutable {
    String getExecutable();

    String execute(String... args) throws Exception;
}
