package io.github.zhangliangbo.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zhangliangbo.external.inner.*;

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
    public static Downloader downloader = new Downloader();
    public static Os os = new Os();
    public static FFmpeg ffmpeg = new FFmpeg();
    public static FFplay ffplay = new FFplay();
    public static Java java = new Java();
}
