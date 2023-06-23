package io.github.zhangliangbo.external.inner;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * @author zhangliangbo
 * @since 2023/4/20
 */
public class IO {

    public Pair<Duration, File> extract(String file, String dir) throws Exception {
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

        File root = null;

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
                } else {
                    if (Objects.isNull(root) && f.isDirectory()) {
                        root = f;
                    }
                }
            } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                } else {
                    if (Objects.isNull(root) && parent.isDirectory()) {
                        root = parent;
                    }
                }
                try (OutputStream outputStream = new FileOutputStream(f)) {
                    IOUtils.copy(archiveInputStream, outputStream);
                }
            }
        }

        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(compressorInputStream);
        IOUtils.closeQuietly(archiveInputStream);

        Instant e = Instant.now();
        return Pair.of(Duration.between(s, e), root);
    }

    public Pair<Duration, File> extract(String file) throws Exception {
        String parent = new File(file).getParent();
        return extract(file, parent);
    }

    public String rootName(String file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        CompressorInputStream compressorInputStream = null;
        try {
            CompressorStreamFactory.detect(inputStream);
            compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(inputStream);
        } catch (Exception e) {
            //ignore
        }
        ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(Objects.isNull(compressorInputStream) ? inputStream : new BufferedInputStream(compressorInputStream));

        int fileNameLength = Integer.MAX_VALUE;
        String root = null;
        try {
            ArchiveEntry entry;
            while (true) {
                entry = archiveInputStream.getNextEntry();
                if (Objects.isNull(entry)) {
                    break;
                }
                String name = entry.getName();
                if (name.length() < fileNameLength) {
                    fileNameLength = name.length();
                    root = name;
                }
            }
            if (Objects.nonNull(root)) {
                String[] split = root.split("/");
                if (ArrayUtils.isNotEmpty(split)) {
                    root = split[0];
                }
            }
            return root;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(compressorInputStream);
            IOUtils.closeQuietly(archiveInputStream);
        }
    }

}
