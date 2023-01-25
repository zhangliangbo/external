package io.github.zhangliangbo.external;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("n").required().hasArg(true).longOpt("name").type(String.class).desc("name").build());
        options.addOption(Option.builder("m").required().hasArg(true).longOpt("method").type(String.class).desc("method").build());
        options.addOption(Option.builder("a").hasArg(true).numberOfArgs(Option.UNLIMITED_VALUES).longOpt("arg").type(String[].class).valueSeparator(',').desc("arg").build());

        DefaultParser parser = new DefaultParser();
        CommandLine parse = parser.parse(options, args);
        String name = parse.getOptionValue("n");
        String method = parse.getOptionValue("m");
        String[] arg = parse.getOptionValues("a");
        Field fieldDefinition = ET.class.getDeclaredField(name);
        fieldDefinition.setAccessible(true);
        Object fieldValue = fieldDefinition.get(ET.class);
        Method myMethod = fieldValue.getClass().getMethod(method);
        Object invoke = ArrayUtils.isEmpty(arg) ? myMethod.invoke(fieldValue) : myMethod.invoke(fieldValue, (Object[]) arg);
        System.out.println(invoke);
    }
}
