package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.zhangliangbo.external.ET;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangliangbo
 * @since 2023-02-04
 */
public class Environment {

    private static final ObjectNode configNode = new ObjectNode(JsonNodeFactory.instance);
    private static final AtomicReference<File> home = new AtomicReference<>(new File(System.getProperty("user.home"), "external"));

    static {
        try {
            initExecutable();

            String userHome = System.getProperty("user.home");
            File configFile = new File(userHome, ".external.json");
            if (configFile.exists()) {
                JsonNode rootNode = ET.objectMapper.readTree(configFile);

                String directory = rootNode.get("dir").asText();
                setHome(directory);

                JsonNode executable = rootNode.get("executable");
                Iterator<String> fieldNames = executable.fieldNames();
                while (fieldNames.hasNext()) {
                    String next = fieldNames.next();
                    JsonNode node = executable.get(next);
                    configNode.set(next, node);
                }
            }
        } catch (IOException e) {
            System.err.printf("加载内置配置文件报错 %s", e);
        }

        if (!home.get().exists()) {
            if (!home.get().mkdirs()) {
                System.err.println("创建主目录失败");
            }
        }
    }

    public static void setExecutable(String name, String executable) {
        ObjectNode jsonNode = (ObjectNode) configNode.get(name);
        if (Objects.isNull(jsonNode)) {
            jsonNode = new ObjectNode(JsonNodeFactory.instance);
            configNode.set(name, jsonNode);
        }
        jsonNode.put(OsType.infer().getCode(), executable);
    }

    public static String getExecutable(String name, String os) {
        JsonNode jsonNode = configNode.get(name);
        if (Objects.isNull(jsonNode)) {
            return null;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        JsonNode executable = objectNode.get(os);
        if (Objects.isNull(executable)) {
            return null;
        }
        return executable.asText();
    }

    public static void setHome(String h) {
        File file = new File(h);
        home.set(file);
    }

    public static File getHome() {
        return home.get();
    }

    private static void initExecutable() {
        //cmd
        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put(OsType.Windows.getCode(), "C:\\Windows\\System32\\cmd.exe");
        configNode.set("cmd", jsonNode);
        //powershell
        try {
            Cmd cmd = new Cmd();
            List<String> list = cmd.where("powershell");
            if (CollectionUtils.isNotEmpty(list)) {
                jsonNode = new ObjectNode(JsonNodeFactory.instance);
                jsonNode.put(OsType.Windows.getCode(), list.get(0));
                configNode.set("powershell", jsonNode);
            }
            Powershell powershell = new Powershell();

            String command = powershell.commandSource("conda");
            jsonNode = new ObjectNode(JsonNodeFactory.instance);
            jsonNode.put(OsType.Windows.getCode(), command);
            configNode.set("conda", jsonNode);

            command = powershell.commandSource("jupyter");
            jsonNode = new ObjectNode(JsonNodeFactory.instance);
            jsonNode.put(OsType.Windows.getCode(), command);
            configNode.set("jupyter", jsonNode);
        } catch (Exception e) {
            //ignore
        }

    }

}
