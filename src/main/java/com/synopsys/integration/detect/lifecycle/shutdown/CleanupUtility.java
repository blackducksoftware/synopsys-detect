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
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupUtility {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void cleanup(@NotNull File directory, @NotNull List<File> skip) throws IOException {
        IOException exception = null;
        logger.debug("Cleaning up directory: " + directory.getAbsolutePath());

        File[] files = directory.listFiles();
        if (files != null) {
            exception = tryDeleteFiles(files, skip);
        }

        deleteDirectoryIfEmpty(directory);

        if (null != exception) {
            throw exception;
        }
    }

    private IOException tryDeleteFiles(@NotNull File[] files, @NotNull List<File> skip) {
        IOException lastException = null;
        for (File file : files) {
            try {
                tryDeleteFile(file, skip);
            } catch (IOException ioe) {
                lastException = ioe;
            }
        }
        return lastException;
    }

    private void tryDeleteFile(File file, @NotNull List<File> skip) throws IOException {
        if (skip.contains(file)) {
            logger.debug("Skipping cleanup for: " + file.getAbsolutePath());
        } else {
            logger.debug("Cleaning up: " + file.getAbsolutePath());
            if (file.getName().contains("status")) {
                logger.info("Status file has been deleted.  To preserve status file, turn off cleanup actions.");
            }
            FileUtils.forceDelete(file);
        }
    }

    private void deleteDirectoryIfEmpty(@NotNull File directory) throws IOException {
        File[] files = directory.listFiles();
        boolean noFiles = files == null || files.length == 0;
        if (noFiles && directory.exists()) {
            logger.info("Cleaning up directory: " + directory.getAbsolutePath());
            FileUtils.forceDelete(directory);
        }
    }
}
