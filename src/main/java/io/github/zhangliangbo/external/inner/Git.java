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

    /**
     * 创建一个新的合并分支
     *
     * @param base  基础分支
     * @param merge 合并进来的分支
     * @return 新分支名称
     * @throws Exception 异常
     */
    public String newMergeBranch(String base, String merge) throws Exception {
        Pair<Integer, String> execute = execute("checkout", merge);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        execute = execute("pull", "--ff-only");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        execute = execute("rev-parse", "--short", "head");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        String sha = execute.getRight();

        execute = execute("checkout", base);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        execute = execute("pull", "--ff-only");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        String newBranchName = base + "-merge-" + merge + "-" + sha + "-" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        execute = execute("checkout", "-b", newBranchName);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        execute = execute("push", "--set-upstream", "origin", newBranchName);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }

        return newBranchName;
    }

    /**
     * 创建一个新的合并分支，当前分支是要合并进来的分支
     *
     * @param base 基础分支
     * @return 新的合并分支
     * @throws Exception 异常
     */
    public String newMergeTo(String base) throws Exception {
        Pair<Integer, String> execute = execute("symbolic-ref", "--short", "HEAD");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        String merge = execute.getRight();
        return newMergeBranch(base, merge);
    }

    /**
     * 更新其他分支并切回来
     *
     * @param branch 待更新分支
     * @return 当前分支
     * @throws Exception 异常
     */
    public String updateBranch(String branch) throws Exception {
        Pair<Integer, String> execute = execute("symbolic-ref", "--short", "HEAD");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        String current = execute.getRight();
        execute = execute("checkout", branch);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        execute = execute("pull", "--ff-only");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        execute = execute("checkout", current);
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return null;
        }
        return current;
    }

    /**
     * 合并后自动提交
     *
     * @throws Exception 异常
     */
    public void afterMerge() throws Exception {
        Pair<Integer, String> execute = execute("add", ".");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return;
        }
        execute = execute("commit", "-m", "\"merge\"");
        System.out.println(execute);
        if (execute.getLeft() != 0) {
            return;
        }
        execute = execute("push", "--force-with-lease");
        System.out.println(execute);
    }

}
