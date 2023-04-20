package io.github.zhangliangbo.external.inner;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * @author zhangliangbo
 * @since 2023/4/20
 */
public class IO {

    public Duration extract(String file, String dir) throws Exception {
        Instant s = Instant.now();

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        CompressorInputStream compressorInputStream = null;
        try {
            CompressorStreamFactory.detect(inputStream);
            compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(inputStream);
        } catch (Exception e) {
            //ignore
        }
        ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(Objects.isNull(compressorInputStream) ? inputStream : new BufferedInputStream(compressorInputStream));

        ArchiveEntry entry;
        while (true) {
            entry = archiveInputStream.getNextEntry();
            if (Objects.isNull(entry)) {
                break;
            }

            String name = entry.getName();

            File f = new File(dir, name);
            if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("failed to create directory " + f);
                }
            } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream outputStream = new FileOutputStream(f)) {
                    IOUtils.copy(archiveInputStream, outputStream);
                }
            }
        }

        org.apache.commons.io.IOUtils.closeQuietly(inputStream);
        org.apache.commons.io.IOUtils.closeQuietly(compressorInputStream);
        org.apache.commons.io.IOUtils.closeQuietly(archiveInputStream);

        Instant e = Instant.now();
        return Duration.between(s, e);
    }

    public Duration extract(String file) throws Exception {
        String parent = new File(file).getParent();
        return extract(file, parent);
    }

}
