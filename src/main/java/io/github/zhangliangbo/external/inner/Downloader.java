package io.github.zhangliangbo.external.inner;

import io.github.zhangliangbo.external.inner.downloader.IDownloader;
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
import java.util.Objects;

/**
 * @author zhangliangbo
 * @since 2023/1/27
 */
public class Downloader implements IDownloader {

    @Override
    public File download(String url) throws IOException {
        String name = FilenameUtils.getName(url);
        File file = new File("D:\\", name);
        int times = 0;
        while (true) {
            try {
                downloadOnce(url, file);
                break;
            } catch (Exception e) {
                System.out.printf("下载报错 开始重试 %s %s\n", ++times, e);
            }
        }
        return file;
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
                        System.out.printf("%s %s/%s\n",
                                BigDecimal.valueOf(receive).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.FLOOR), receive, total);
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
                            System.out.printf("%s %s/%s\n",
                                    BigDecimal.valueOf(receive).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.FLOOR), receive, total);
                        }
                        fos.close();
                        content.close();
                        return file;
                    }
                });
    }

}
