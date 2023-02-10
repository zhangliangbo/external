package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

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
        Pair<Integer, String> pair = execute("-v", "quiet", "-version");
        return pair.getRight();
    }

    public String buildConf() throws Exception {
        Pair<Integer, String> pair = execute("-v", "quiet", "-buildconf");
        return pair.getRight();
    }

    public String formats() throws Exception {
        Pair<Integer, String> pair = execute("-v", "quiet", "-formats");
        return pair.getRight();
    }

    public String devices() throws Exception {
        Pair<Integer, String> pair = execute("-v", "quiet", "-devices");
        return pair.getRight();
    }

    public List<String> cameras() throws Exception {
        Pair<Integer, String> pair = execute("-v", "info", "-f", "dshow", "-list_devices", "true", "-i", "dummy");
        String res = pair.getRight();
        String[] split = res.split("\n");

        List<String> ans = new LinkedList<>();
        for (String s : split) {
            if (s.contains("dshow") && !s.contains("Alternative name")) {
                int right = s.lastIndexOf("]");
                String trim = s.substring(right);
                ans.add(trim);
            }
        }
        return ans;
    }

}
