package io.github.zhangliangbo.external.inner.downloader;

import java.io.File;
import java.io.IOException;

/**
 * @author zhangliangbo
 * @since 2023/1/27
 */
public interface IDownloader {
    File download(String url, String dest, String sleepInterval) throws IOException;

    File download(String url, String dest) throws IOException;
}
