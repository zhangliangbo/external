package io.github.zhangliangbo.external.inner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArchUtils;

/**
 * @author zhangliangbo
 * @since 2023/1/27
 */
@Slf4j
public class Os {
    public String arch() {
        log.info("arch");
        return ArchUtils.getProcessor().getArch().getLabel();
    }

    public String type() {
        log.info("type");
        return ArchUtils.getProcessor().getType().name();
    }
}
