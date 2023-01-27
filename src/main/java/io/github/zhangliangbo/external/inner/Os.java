package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.ArchUtils;

/**
 * @author zhangliangbo
 * @since 2023/1/27
 */
public class Os {
    public String arch() {
        return ArchUtils.getProcessor().getArch().getLabel();
    }

    public String type() {
        return ArchUtils.getProcessor().getType().name();
    }
}
