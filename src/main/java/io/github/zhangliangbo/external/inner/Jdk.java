package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangliangbo
 * @since 2023/3/12
 */
public class Jdk extends AbstractExternalExecutable {

    @Override
    public String getName() {
        return "jdk";
    }

    @Override
    public String getExecutable(String name) throws Exception {
        String directory = super.getExecutable(name);
        File root = new File(directory, "bin");
        return searchExecutable(root, name);
    }

    public Pair<Integer, String> runJar(String jar, String method) throws Exception {
        List<String> argList = new LinkedList<>();
        argList.add("-agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image");
        argList.add("-jar");
        argList.add(jar);
        argList.add(method);
        String[] newArgs = argList.toArray(new String[]{});
        return executeSub("java", newArgs);
    }

    public Pair<Integer, String> version() throws Exception {
        return executeSub("java", "-version");
    }

    public Pair<Integer, JsonNode> flagsFinal() throws Exception {
        Pair<Integer, String> pair = executeSub("java", "-XX:+PrintFlagsFinal", "-version");
        return processFlags(pair);
    }

    public Pair<Integer, String> listProcess() throws Exception {
        return executeSub("jcmd", "-l");
    }

    public Pair<Integer, String> listProcessCommand(String pid) throws Exception {
        return executeSub("jcmd", pid);
    }

    public Pair<Integer, String> processCommandHelp(String pid, String command) throws Exception {
        return executeSub("jcmd", pid, "help", command);
    }

    public Pair<Integer, String> processCommand(String pid, String command) throws Exception {
        return executeSub("jcmd", pid, command);
    }

    public Pair<Integer, String> processCommandFlag(String pid) throws Exception {
        return executeSub("jcmd", pid, "VM.flags");
    }

    public Pair<Integer, JsonNode> flagsInitial() throws Exception {
        Pair<Integer, String> pair = executeSub("java", "-XX:+PrintFlagsInitial", "-version");
        return processFlags(pair);
    }

    private Pair<Integer, JsonNode> processFlags(Pair<Integer, String> pair) throws IOException {
        if (pair.getKey() != 0) {
            return null;
        }
        String data = pair.getValue();
        StringReader stringReader = new StringReader(data);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        ObjectNode ans = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        String name = "flags";

        String line = bufferedReader.readLine();
        while (Objects.nonNull(line)) {
            if (line.contains("[") || line.contains("]")) {
                name = line;
            } else {
                String[] split = line.trim().split(" +", 5);
                ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
                objectNode.put("type", split[0]);
                objectNode.put("flag", split[1]);
                objectNode.put("symbol", split[2]);
                if (split.length == 4) {
                    objectNode.put("value", "");
                    objectNode.put("extra", split[3]);
                } else {
                    objectNode.put("value", split[3]);
                    objectNode.put("extra", split[4]);
                }
                arrayNode.add(objectNode);
            }
            line = bufferedReader.readLine();
        }
        ans.set(name, arrayNode);
        return Pair.of(pair.getLeft(), ans);
    }

    public Pair<Integer, JsonNode> commandLineFlags() throws Exception {
        Pair<Integer, String> pair = executeSub("java", "-XX:+PrintCommandLineFlags", "-version");
        if (pair.getKey() != 0) {
            return null;
        }
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        String value = pair.getValue();
        String[] split = value.split(" +");
        for (String s : split) {
            arrayNode.add(s);
        }
        return Pair.of(pair.getLeft(), arrayNode);
    }

}
