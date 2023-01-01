package io.github.zhangliangbo.external;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        String execute = ET.git.execute("help", "branch");
        System.out.println(execute);
        int read = System.in.read();
        System.out.println(read);
    }
}
