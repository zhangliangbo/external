package io.github.zhangliangbo.external.inner;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.zhangliangbo.external.ET;

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

    @Override
    public File getExecutableHome() throws Exception {
        OsType infer = OsType.infer();
        File executableFile = null;
        if (map.containsKey(infer)) {
            executableFile = map.get(infer);
        } else {
            String executable = Environment.getExecutable(getName(), infer.getCode());
            if (Objects.nonNull(executable)) {
                executableFile = new File(executable);
            }
            map.put(infer, executableFile);
        }
        if (Objects.isNull(executableFile)) {
            throw new Exception("executable为空");
        }
        return executableFile;
    }

    @Override
    public String getExecutable() throws Exception {
        File executableFile = getExecutableHome();
        if (executableFile.isDirectory()) {
            throw new Exception("executable不是文件");
        }
        return executableFile.getAbsolutePath();
    }

    @Override
    public String getExecutable(String name) throws Exception {
        File executableFile = getExecutableHome();
        if (executableFile.isFile()) {
            throw new Exception("executable不是目录");
        }
        return executableFile.getAbsolutePath();
    }

}
