package io.github.zhangliangbo.external.inner;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import reactor.netty.http.client.HttpClient;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * https://projectreactor.io/docs/netty/release/reference/index.html
 *
 * @author zhangliangbo
 * @since 2023/1/27
 */
@Slf4j
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

    public static void main(String[] args) throws IOException {
        new Downloader().downloadOnce("https://downloads.apache.org/kafka/3.4.0/kafka_2.13-3.4.0.tgz", new File("D:\\"));
    }

    private void downloadOnce(String url, File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();

        LongAdder receive = new LongAdder();
        LongAdder position = new LongAdder();

        AtomicLong total = new AtomicLong(0L);

        log.info("start request");

        HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .doOnResponse((x, y) -> {
                    log.info("get response");

                    HttpHeaders entries = x.responseHeaders();
                    String contentLength = entries.getAsString("Content-Length");
                    long l = Long.parseLong(contentLength);
                    total.set(l);
                    BigDecimal progress = BigDecimal.valueOf(receive.longValue()).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total.get()), 2, RoundingMode.FLOOR);
                    System.out.printf("%s/%s %s%%", receive, total, progress);
                })
                .wiretap("cute")
                .get()
                .uri(url)
                .responseContent()
                .doOnNext(t -> {
                    try {
                        int len = t.readableBytes();
                        t.readBytes(channel, position.longValue(), len);
                        position.add(len);
                        receive.add(len);
                        BigDecimal progress = BigDecimal.valueOf(receive.longValue()).multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(total.get()), 2, RoundingMode.FLOOR);
                        System.out.printf("\r%s/%s %s%%", receive, total, progress);
                    } catch (IOException e) {
                        System.err.printf("写文件报错%s\n", e.getMessage());
                    }
                })
                .blockLast();

        randomAccessFile.close();

        System.out.println();
    }

}
