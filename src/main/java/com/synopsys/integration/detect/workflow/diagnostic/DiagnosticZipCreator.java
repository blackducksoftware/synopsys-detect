package com.synopsys.integration.detect.workflow.diagnostic;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticZipCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean createDiagnosticZip(String runId, File outputDirectory, List<File> compressList) {
        try {
            String zipPath = "detect-run-" + runId + ".zip";
            File zip = new File(outputDirectory, zipPath);
            logger.info("Diagnostics zip location: " + zip.toPath());
            try (FileOutputStream fileStream = new FileOutputStream(zip)) {
                try (ZipOutputStream outputStream = new ZipOutputStream(fileStream)) {
                    for (File file : compressList) {
                        compress(outputStream, outputDirectory.toPath(), file.toPath(), runId);
                    }
                    logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to create zip.", e);
        }
        return false;
    }

    // Remove names matching toRemove from the given path and create a new Path of those pieces.
    // This is because the path to a file is /container/runId/file.txt but the zip will already be named runId
    // So the file should be added to the zip as /container/file.txt
    // Sorry - jordan 7/16/2018 - plz make better
    private String toZipEntryName(Path path, String toRemove) {
        try {
            List<String> pieces = new ArrayList<>();
            for (int i = 0; i < path.getNameCount(); i++) {
                String next = path.getName(i).toString();
                if (!next.equals(toRemove)) {
                    pieces.add(next);
                }
            }

            return String.join("/", pieces);
        } catch (Exception e) {
            logger.info("Failed to clean zip entry.");
            return path.toString();
        }
    }

    public void compress(ZipOutputStream outputStream, Path sourceDir, Path toCompress, String removePiece) throws IOException {
        Files.walkFileTree(toCompress, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                try {
                    Path targetFile = sourceDir.relativize(file);
                    String target = toZipEntryName(targetFile, removePiece);
                    logger.debug("Adding file to zip: " + target);
                    outputStream.putNextEntry(new ZipEntry(target));
                    byte[] bytes = Files.readAllBytes(file);
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.closeEntry();
                } catch (IOException e) {
                    logger.error("Failed to write to zip.", e);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
