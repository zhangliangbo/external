package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.zhangliangbo.external.ET;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangliangbo
 * @since 2023-02-04
 */
public class Environment {

    private static final ObjectNode configNode = new ObjectNode(JsonNodeFactory.instance);
    private static final AtomicReference<File> home = new AtomicReference<>(new File(System.getProperty("user.home"), "external"));
    private static final Cmd cmd = new Cmd();
    private static final Powershell powershell = new Powershell();

    static {
        try {
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

        if (!getHome().exists()) {
            if (!getHome().mkdirs()) {
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

    public static String getExecutable(ExternalExecutable externalExecutable, String os) throws Exception {
        JsonNode jsonNode = configNode.get(externalExecutable.getName());
        if (Objects.isNull(jsonNode)) {
            //没有注册，则自动检测
            String executable = externalExecutable.autoDetect(cmd, powershell);
            //自动检测不到，就说明没有
            if (StringUtils.isBlank(executable)) {
                return null;
            }
            //缓存起来
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.put(OsType.infer().getCode(), executable);
            configNode.set(externalExecutable.getName(), objectNode);
            //指定可执行文件
            jsonNode = objectNode;
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

    public static File searchLocal(String name) {
        File home = getHome();
        File[] files = home.listFiles();
        if (Objects.isNull(files)) {
            return null;
        }
        for (File file : files) {
            if (file.getName().contains(name)) {
                return file;
            }
        }
        return null;
    }

}
