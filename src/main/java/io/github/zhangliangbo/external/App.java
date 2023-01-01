package io.github.zhangliangbo.external;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("user.dir"));
        int read = System.in.read();
        System.out.println(read);
    }
}
