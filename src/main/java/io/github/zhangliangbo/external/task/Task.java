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
        //安装git
        List<String> git = ET.cmd.where("git");
        if (CollectionUtils.isNotEmpty(git)) {
            System.out.printf("%s已安装，跳过\n", "git");
        } else {
            result = ET.scoop.installApp("git");
            System.out.println(result);
        }
        //添加所有的仓库
        List<String> buckets = ET.scoop.bucketAll();
        for (String bucket : buckets) {
            boolean res = ET.scoop.bucketAdd(bucket);
            System.out.printf("添加%s结果%s\n", bucket, res);
        }
        //更新
        Boolean update = ET.scoop.update();
        System.out.printf("更新%s结果%s\n", "scoop", update);
        //安装anaconda3
    }
}
