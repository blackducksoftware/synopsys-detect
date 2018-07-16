package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticZipCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean createDiagnosticZip(final String runId, final File outputDirectory, final List<File> compressList) {
        logger.info("Creating diagnostics zip.");
        try {
            final File zip = new File(outputDirectory, "detect-run-" + runId + ".zip");
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
            for (final File file : compressList) {
                compress(outputStream, outputDirectory.toPath(), file.toPath(), zip);
            }
            logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
            outputStream.close();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void compress(final ZipOutputStream outputStream, final Path sourceDir, final Path toCompress, final File out) throws IOException {
        Files.walkFileTree(toCompress, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) {
                try {
                    final Path targetFile = sourceDir.relativize(file);
                    final String target = targetFile.toString();
                    logger.debug("Adding file to zip: " + target);
                    outputStream.putNextEntry(new ZipEntry(target));
                    final byte[] bytes = Files.readAllBytes(file);
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.closeEntry();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
