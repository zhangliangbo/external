package io.github.zhangliangbo.external;

import io.github.zhangliangbo.external.inner.Environment;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EventListener;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("i").required().hasArg(true).longOpt("invoke").type(String.class).desc("invoke").build());
        options.addOption(Option.builder("c").hasArg(true).longOpt("config").type(String.class).desc("config").build());
        options.addOption(Option.builder("h").hasArg(true).longOpt("home").type(String.class).desc("home").build());

        DefaultParser parser = new DefaultParser();
        CommandLine parse = parser.parse(options, args);
        String invokeMethod = parse.getOptionValue("i");
        String config = parse.getOptionValue("c");
        String home = parse.getOptionValue("h");

        if (StringUtils.isNotBlank(home)) {
            Environment.setHome(home);
        }

        String dot = ".";
        int dotPos = invokeMethod.indexOf(dot);
        String left = "[";
        int leftPos = invokeMethod.indexOf(left);
        String right = "]";
        int rightPos = invokeMethod.indexOf(right);

        String name = invokeMethod.substring(0, dotPos);

        //手动指定可执行文件
        if (StringUtils.isNotBlank(config)) {
            Environment.setExecutable(name, config);
        }

        String method = invokeMethod.substring(dotPos + 1, leftPos > -1 ? leftPos : invokeMethod.length());
        String[] arg = new String[0];
        if (leftPos > -1 && rightPos > -1) {
            String argString = invokeMethod.substring(leftPos + 1, rightPos);
            if (StringUtils.isNotBlank(argString)) {
                arg = argString.split(",");
            }
        }

        Field fieldDefinition = ET.class.getDeclaredField(name);
        fieldDefinition.setAccessible(true);
        Object fieldValue = fieldDefinition.get(ET.class);

        Object invoke;
        if (ArrayUtils.isEmpty(arg)) {
            Method myMethod = fieldValue.getClass().getMethod(method);
            invoke = myMethod.invoke(fieldValue);
        } else {
            Class<?>[] cls = new Class[arg.length];
            for (int i = 0; i < cls.length; i++) {
                cls[i] = arg[i].getClass();
            }
            Method myMethod = fieldValue.getClass().getMethod(method, cls);
            invoke = myMethod.invoke(fieldValue, (Object[]) arg);
        }

        System.out.println(invoke);
    }
}
