package io.github.zhangliangbo.external.task;

import io.github.zhangliangbo.external.ET;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangliangbo
 * @since 2023/6/14
 */
public class Task {
    public void deployNewMachine() throws Exception {
        String result;
        List<String> scoop = ET.cmd.where("scoop");
        if (CollectionUtils.isNotEmpty(scoop)) {
            System.out.printf("%s已安装，跳过\n", "scoop");
        } else {
            result = ET.powershell.installScoop();
            System.out.println(result);
        }
        //修改repo
        boolean b = ET.scoop.changeScoopRepo();
        System.out.printf("更新%s结果%s\n", "repo", b);
        //安装git
        List<String> git = ET.cmd.where("git");
        if (CollectionUtils.isNotEmpty(git)) {
            System.out.printf("%s已安装，跳过\n", "git");
        } else {
            result = ET.scoop.installApp("git");
            System.out.println(result);
        }
        //添加仓库
        b = ET.scoop.bucketAdd("extras");
        System.out.printf("添加%s结果%s\n", "extras", b);
        //更新
        Boolean update = ET.scoop.update("scoop");
        System.out.printf("更新%s结果%s\n", "scoop", update);
        //安装anaconda3
        List<String> python = ET.cmd.where("python");
        if (CollectionUtils.isNotEmpty(python)) {
            System.out.printf("%s已安装，跳过\n", "anaconda3");
        } else {
            result = ET.scoop.installApp("anaconda3");
            System.out.println(result);
        }

        String git1 = ET.scoop.bucket("git");
        System.out.println(git1);
    }
}
