package io.github.zhangliangbo.external.inner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

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
            String executable = Environment.getExecutable(this, infer.getCode());
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

    protected String searchExecutable(File root, String name) throws Exception {
        File[] files = root.listFiles();
        if (files != null) {
            List<String> extensions = new LinkedList<>();
            OsType infer = OsType.infer();
            if (infer == OsType.Windows) {
                extensions.add(".exe");
                extensions.add(".cmd");
            }
            for (File file : files) {
                String fileName = FilenameUtils.getName(file.getAbsolutePath());
                boolean match;
                if (extensions.isEmpty()) {
                    match = Objects.equals(fileName, name);
                } else {
                    match = extensions.stream().map(t -> name + t)
                            .anyMatch(t -> Objects.equals(t, fileName));
                }
                if (match) {
                    return file.getAbsolutePath();
                }
            }
        }
        throw new Exception(String.format("%s未找到", name));
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell, String name) throws Exception {
        return powershell.commandSource(StringUtils.isBlank(name) ? getName() : name);
    }

    @Override
    public String autoDetect(Cmd cmd, Powershell powershell) throws Exception {
        return autoDetect(cmd, powershell, getName());
    }

}
