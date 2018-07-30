/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static File getRootDir(final File givenDir, int depth) {
        logger.debug(String.format("givenDir: %s; depth: %d", givenDir, depth));
        File rootDir = givenDir;
        for (; depth > 0; depth--) {
            rootDir = rootDir.getParentFile();
        }
        logger.debug(String.format("rootDir: %s", rootDir));
        return rootDir;
    }

    public static boolean isUnder(final File dir, final File file) {
        logger.trace(String.format("Checking to see if file %s is under dir %s", file.getAbsolutePath(), dir.getAbsolutePath()));
        try {
            final String dirPath = dir.getCanonicalPath();
            final String filePath = file.getCanonicalPath();
            logger.trace(String.format("\tactually comparing file path %s with dir path %s", filePath, dirPath));
            if (filePath.equals(dirPath) || filePath.startsWith(dirPath)) {
                logger.trace(String.format("\t%s is under %s", file.getAbsolutePath(), dir.getAbsolutePath()));
                return true;
            }
            logger.trace(String.format("\t%s is not under %s", file.getAbsolutePath(), dir.getAbsolutePath()));
            return false;
        } catch (final IOException e) {
            logger.warn(String.format("Error getting canonical path for either %s or %s", dir.getAbsolutePath(), file.getAbsolutePath()));
            return false;
        }
    }
}
