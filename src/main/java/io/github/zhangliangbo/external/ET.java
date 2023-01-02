package io.github.zhangliangbo.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zhangliangbo.external.inner.Exec;
import io.github.zhangliangbo.external.inner.Git;
import io.github.zhangliangbo.external.inner.Scoop;

/**
 * @author zhangliangbo
 * @since 2022/12/31
 */
public class ET {
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static Exec exec = new Exec();
    public static Scoop scoop = new Scoop();
    public static Git git = new Git();
}
