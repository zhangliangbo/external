package io.github.zhangliangbo.external;

import java.io.File;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println(ET.kafka.changeProperty(
                new File("D:\\kafka_2.12-3.3.1\\config\\kraft\\server.properties"),
                "node.id", "15"
        ));
    }
}
