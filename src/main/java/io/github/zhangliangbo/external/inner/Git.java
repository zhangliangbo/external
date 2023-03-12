package io.github.zhangliangbo.external.inner;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public class Git extends AbstractExternalExecutable {
    @Override
    public String getName() {
        return "git";
    }

    public void newMergeBranch(String base, String merge) throws Exception {
        Pair<Integer, String> execute = execute("checkout", merge);
        System.out.println(execute);
        execute = execute("pull", "--ff-only");
        System.out.println(execute);
        execute = execute("checkout", base);
        System.out.println(execute);
        execute = execute("pull", "--ff-only");
        System.out.println(execute);
        String newBranchName = base + "-merge-" + merge + "-" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        execute = execute("checkout", "-b", newBranchName);
        System.out.println(execute);
        execute = execute("push", "--set-upstream", "origin", newBranchName);
        System.out.println(execute);
    }

}
