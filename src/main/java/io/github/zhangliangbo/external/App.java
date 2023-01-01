package io.github.zhangliangbo.external;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Pair<Integer, String> pair = ET.scoop.execute("help");
        System.out.println(pair.getKey());
        System.out.println(pair.getValue());
        System.out.println("enter key to exist:\n");
        int read = System.in.read();
        System.out.println(read);
    }
}
