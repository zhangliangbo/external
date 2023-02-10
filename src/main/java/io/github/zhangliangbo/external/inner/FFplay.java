package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhangliangbo
 * @since 2023-02-06
 */
public class FFplay extends AbstractExternalExecutable {
    @Override
    public String getName() {
        return "ffplay";
    }

    public void playCamera(String name) throws Exception {
        Pair<Integer, String> pair = execute("-v", "quiet", "-f", "dshow", "-i", "video=" + name);
        System.out.println(pair.getRight());
    }

}
