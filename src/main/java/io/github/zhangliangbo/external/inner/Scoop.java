package io.github.zhangliangbo.external.inner;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public class Scoop extends AbstractExternalExecutable{
    @Override
    public String getExecutable() {
        return "D:\\scoop\\shims\\scoop.cmd";
    }
}
