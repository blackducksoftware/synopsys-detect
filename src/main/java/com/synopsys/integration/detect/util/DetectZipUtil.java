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
package com.synopsys.integration.detect.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectZipUtil {
    private static final Logger logger = LoggerFactory.getLogger(DetectZipUtil.class);

    public static void unzip(final File zip, final File dest) throws IOException {
        unzip(zip, dest, Charset.defaultCharset());
    }

    public static void zip(final File zip, final Map<String, Path> entries) throws IOException {
        try (final FileOutputStream fileStream = new FileOutputStream(zip)) {
            try (final ZipOutputStream outputStream = new ZipOutputStream(fileStream)) {
                for (final Map.Entry<String, Path> entry : entries.entrySet()) {
                    logger.info("Adding entry '{}' to zip as '{}'.", entry.getValue().toString(), entry.getKey());
                    outputStream.putNextEntry(new ZipEntry(entry.getKey()));
                    final byte[] bytes = Files.readAllBytes(entry.getValue());
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.closeEntry();
                }
            }
        }
    }

    public static void unzip(final File zip, final File dest, final Charset charset) throws IOException {
        final Path destPath = dest.toPath();
        try (final ZipFile zipFile = new ZipFile(zip, ZipFile.OPEN_READ, charset)) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final Path entryPath = destPath.resolve(entry.getName());
                if (!entryPath.normalize().startsWith(dest.toPath()))
                    throw new IOException("Zip entry contained path traversal");
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (final InputStream in = zipFile.getInputStream(entry)) {
                        try (final OutputStream out = new FileOutputStream(entryPath.toFile())) {
                            IOUtils.copy(in, out);
                        }
                    }
                }
            }
        }
    }
}
