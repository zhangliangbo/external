package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023-02-06
 */
public class FFmpeg extends AbstractExternalExecutable {
    @Override
    public String getName() {
        return "ffmpeg";
    }

    public String version() throws Exception {
        Pair<Integer, String> pair = execute(null, null, 0, "-v", "quiet", "-version");
        return pair.getRight();
    }

    public String buildConf() throws Exception {
        Pair<Integer, String> pair = execute(null, null, 0, "-v", "quiet", "-buildconf");
        return pair.getRight();
    }

    public String formats() throws Exception {
        Pair<Integer, String> pair = execute(null, null, 0, "-v", "quiet", "-formats");
        return pair.getRight();
    }

    public String devices() throws Exception {
        Pair<Integer, String> pair = execute(null, null, 0, "-v", "quiet", "-devices");
        return pair.getRight();
    }

}
