package io.github.zhangliangbo.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zhangliangbo.external.inner.*;
import io.github.zhangliangbo.external.task.Task;

/**
 * @author zhangliangbo
 * @since 2022/12/31
 */
public class ET {
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static Exec exec = new Exec();
    public static Scoop scoop = new Scoop();
    public static Git git = new Git();
    public static Kafka kafka = new Kafka();
    public static Wsl wsl = new Wsl();
    public static Http http = new Http();
    public static Os os = new Os();
    public static FFmpeg ffmpeg = new FFmpeg();
    public static FFplay ffplay = new FFplay();
    public static Jdk jdk = new Jdk();
    public static Node node = new Node();
    public static Cmd cmd = new Cmd();
    public static IO io = new IO();
    public static Powershell powershell = new Powershell();
    public static Task task = new Task();
    public static Conda conda = new Conda();
    public static Jupyter jupyter = new Jupyter();
    public static Choco choco = new Choco();
    public static WinGet winget = new WinGet();
}
