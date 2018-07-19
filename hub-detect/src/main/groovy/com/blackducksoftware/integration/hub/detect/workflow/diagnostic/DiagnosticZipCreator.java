package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticZipCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean createDiagnosticZip(final String runId, final File outputDirectory, final List<File> compressList) {
        try {
            final String zipPath = "detect-run-" + runId + ".zip";
            final File zip = new File(outputDirectory, zipPath);
            logger.info("Diagnostics zip location: " + zip.toPath());
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
            for (final File file : compressList) {
                compress(outputStream, outputDirectory.toPath(), file.toPath(), zip, runId);
            }
            logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
            outputStream.close();
            return true;
        } catch (final Exception e) {
            logger.error("Failed to create zip.", e);
        }
        return false;
    }

    // Remove names matching toRemove from the given path and create a new Path of those pieces.
    // This is because the path to a file is /container/runId/file.txt but the zip will already be named runId
    // So the file should be added to the zip as /container/file.txt
    // Sorry - jordan 7/16/2018 - plz make better
    private String toZipEntryName(final Path path, final String toRemove) {
        try {
            final List<String> pieces = new ArrayList<>();
            for (int i = 0; i < path.getNameCount(); i++) {
                final String next = path.getName(i).toString();
                if (!next.equals(toRemove)) {
                    pieces.add(next);
                }
            }

            return pieces.stream().collect(Collectors.joining("/"));
        } catch (final Exception e) {
            logger.info("Failed to clean zip entry.");
            return path.toString();
        }
    }

    public void compress(final ZipOutputStream outputStream, final Path sourceDir, final Path toCompress, final File out, final String removePiece) throws IOException {
        Files.walkFileTree(toCompress, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) {
                try {
                    final Path targetFile = sourceDir.relativize(file);
                    final String target = toZipEntryName(targetFile, removePiece);
                    logger.debug("Adding file to zip: " + target);
                    outputStream.putNextEntry(new ZipEntry(target));
                    final byte[] bytes = Files.readAllBytes(file);
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.closeEntry();
                } catch (final IOException e) {
                    logger.error("Failed to write to zip.", e);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
