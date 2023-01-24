package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.inner.kafka.IKafka;
import io.github.zhangliangbo.external.inner.wsl.IWsl;

/**
 * @author zhangliangbo
 * @since 2023/1/24
 */
public class Wsl extends AbstractExternalExecutable implements IWsl {
    @Override
    public String getName() {
        return "wsl";
    }
}
