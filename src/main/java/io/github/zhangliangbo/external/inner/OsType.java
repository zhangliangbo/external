package io.github.zhangliangbo.external.inner;

import org.apache.commons.exec.OS;

/**
 * @author zhangliangbo
 * @since 2023/1/1
 */
public enum OsType {

    Unknown(""),
    Windows("windows"),
    Unix("unix"),
    Mac("mac");

    private final String code;

    OsType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static OsType infer() {
        if (OS.isFamilyWindows()) {
            return Windows;
        } else if (OS.isFamilyUnix()) {
            return Unix;
        } else if (OS.isFamilyMac()) {
            return Mac;
        } else {
            return Unknown;
        }
    }

}
