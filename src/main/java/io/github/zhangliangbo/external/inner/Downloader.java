package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.inner.downloader.ClientDownloadFileInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.io.FilenameUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.util.Timeout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * https://projectreactor.io/docs/netty/release/reference/index.html
 * @author zhangliangbo
 * @since 2023/1/27
 */
public class Downloader {

    public File download(String url, String dest, String sleepInterval) throws IOException {
        String name = FilenameUtils.getName(url);
        File destFile = new File(dest);
        File file = destFile.isDirectory() ? new File(destFile, name) : destFile;
        int times = 0;
        while (true) {
            try {
                downloadOnce(url, file);
                break;
            } catch (Exception e) {
                long second = Long.parseLong(sleepInterval);
                System.out.printf("\n下载报错 %s后开始重试 %s %s\n", Duration.ofSeconds(second), ++times, e);
                while (second > 0) {
                    try {
                        System.out.printf("\r剩余%s秒", second--);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.print("\r重试开始\n");
            }
        }
        return file;
    }

    public File download(String url, String dest) throws IOException {
        return download(url, dest, "60");
    }

    public File download(String url) throws IOException {
        return download(url, Environment.getHome().getAbsolutePath(), "60");
    }

    private void downloadOnce(String url, File file) throws IOException {
        Request.get(url)
                .connectTimeout(Timeout.ofMinutes(1))
                .responseTimeout(Timeout.ofMinutes(1))
                .execute()
                .handleResponse(new HttpClientResponseHandler<File>() {
                    @Override
                    public File handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
                        HttpEntity entity = response.getEntity();
                        if (Objects.isNull(entity)) {
                            throw new IOException("请求体为空");
                        }
                        long total = response.getEntity().getContentLength();
                        long receive = 0;
                        BigDecimal progress = BigDecimal.valueOf(receive).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.FLOOR);
                        System.out.printf("%s/%s %s%%", receive, total, progress);
                        InputStream content = entity.getContent();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[4 * 1024];
                        while (true) {
                            int len = content.read(buffer);
                            if (len < 0) {
                                break;
                            }
                            fos.write(buffer, 0, len);
                            receive += len;
                            progress = BigDecimal.valueOf(receive).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.FLOOR);
                            System.out.printf("\r%s/%s %s%%", receive, total, progress);
                        }
                        System.out.print("\n");
                        fos.close();
                        content.close();
                        return file;
                    }
                });
    }

    private void downloadOnce2(String url, File file) throws IOException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientDownloadFileInitializer());
        ChannelFuture connect = bootstrap.connect("127.0.0.1", 8080);
    }

    private void downloadOnce3(String url, File file) throws IOException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientDownloadFileInitializer());
        ChannelFuture connect = bootstrap.connect("127.0.0.1", 8080);
    }

}
