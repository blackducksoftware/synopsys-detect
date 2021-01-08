/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
            try (FileOutputStream fileStream = new FileOutputStream(zip)) {
                try (ZipOutputStream outputStream = new ZipOutputStream(fileStream)) {
                    for (final File file : compressList) {
                        compress(outputStream, outputDirectory.toPath(), file.toPath(), runId);
                    }
                    logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
                }
            }
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

            return String.join("/", pieces);
        } catch (final Exception e) {
            logger.info("Failed to clean zip entry.");
            return path.toString();
        }
    }

    public void compress(final ZipOutputStream outputStream, final Path sourceDir, final Path toCompress, final String removePiece) throws IOException {
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
