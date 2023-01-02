package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.ET;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public abstract class AbstractExternalExecutable implements ExternalExecutable {

    private final Map<OsType, String> map = new HashMap<>();
    private Function<String, String> factory;

    @Override
    public String getExecutable() throws Exception {
        OsType infer = OsType.infer();
        String executable;
        if (map.containsKey(infer)) {
            executable = map.get(infer);
        } else {
            Function<String, String> executableFactory = getExecutableFactory();
            if (Objects.isNull(executableFactory)) {
                throw new Exception("ExecutableFactory为空");
            }
            executable = executableFactory.apply(infer.getCode());
            map.put(infer, executable);
        }
        if (StringUtils.isBlank(executable)) {
            throw new Exception("executable为空");
        }
        return executable;
    }

    @Override
    public void setExecutableFactory(Function<String, String> function) {
        this.factory = function;
    }

    @Override
    public Function<String, String> getExecutableFactory() {
        return factory;
    }

    @Override
    public Pair<Integer, String> execute(String directory, long timeout, String... args) throws Exception {
        return ET.exec.execute(this, directory, timeout, args);
    }

    @Override
    public Pair<Integer, String> execute(long timeout, String... args) throws Exception {
        return ET.exec.execute(this, null, timeout, args);
    }

    @Override
    public Pair<Integer, String> execute(String... args) throws Exception {
        return ET.exec.execute(this, null, 0, args);
    }
}
