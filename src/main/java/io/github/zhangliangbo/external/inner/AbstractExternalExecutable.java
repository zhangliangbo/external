package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.zhangliangbo.external.ET;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public abstract class AbstractExternalExecutable implements ExternalExecutable {

    private final Map<OsType, File> map = new HashMap<>();
    private Function<String, File> factory;

    @Override
    public File getExecutableFile() throws Exception {
        OsType infer = OsType.infer();
        File executable;
        if (map.containsKey(infer)) {
            executable = map.get(infer);
        } else {
            Function<String, File> executableFactory = getExecutableFactory();
            if (Objects.isNull(executableFactory)) {
                JsonNode rootNode = ET.objectMapper.readTree(ClassLoader.getSystemResourceAsStream("executable.json"));
                JsonNode executableNode = rootNode.get(getName());
                if (Objects.nonNull(executableNode)) {
                    executableFactory = os -> new File(executableNode.get(os).asText());
                }
            }
            if (Objects.isNull(executableFactory)) {
                throw new Exception("ExecutableFactory为空");
            }
            executable = executableFactory.apply(infer.getCode());
            map.put(infer, executable);
        }
        if (Objects.isNull(executable)) {
            throw new Exception("executable为空");
        }
        return executable;
    }

    @Override
    public String getExecutable() throws Exception {
        File executableFile = getExecutableFile();
        if (executableFile.isDirectory()) {
            throw new Exception("executable不是文件");
        }
        return executableFile.getAbsolutePath();
    }

    @Override
    public String getExecutable(String name) throws Exception {
        File executableFile = getExecutableFile();
        if (executableFile.isFile()) {
            throw new Exception("executable不是目录");
        }
        return executableFile.getAbsolutePath();
    }

    @Override
    public void setExecutableFactory(Function<String, File> function) {
        this.factory = function;
    }

    @Override
    public Function<String, File> getExecutableFactory() {
        return factory;
    }

    @Override
    public Pair<Integer, String> execute(Map<String, String> env, String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(env, getExecutable(), directory, timeout, args);
    }

    @Override
    public Pair<Integer, String> execute(Map<String, String> env, String name, String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(env, getExecutable(name), directory, timeout, args);
    }

}
