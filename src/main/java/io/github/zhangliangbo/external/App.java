package io.github.zhangliangbo.external;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        String executable = ET.kafka.generateClusterID();
        System.out.println(executable);
    }
}
