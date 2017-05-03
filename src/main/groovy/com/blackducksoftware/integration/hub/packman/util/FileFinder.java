/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileFinder {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean containsAllFiles(final String sourcePath, final File... files) {
        return containsAllFiles(sourcePath, Arrays.asList(files));
    }

    public boolean containsAllFiles(final String sourcePath, final List<File> files) {
        if (StringUtils.isBlank(sourcePath)) {
            return false;
        }

        final File sourceDirectory = new File(sourcePath);
        boolean containsFiles = true;
        if (sourceDirectory.isDirectory()) {
            for (final File file : files) {
                final File foundFile = findFile(sourceDirectory.getAbsolutePath(), file);
                if (foundFile == null) {
                    containsFiles = false;
                    logger.debug("Couldn't find a neccesary file >" + file.getPath());
                    break;
                }
            }
        }
        return containsFiles;
    }

    public File findFile(final String sourcePath, final File file) {
        final File sourceDirectory = new File(sourcePath);
        final String fileName = file.getPath();
        File foundFile = new File(sourceDirectory, fileName);
        if (fileName.startsWith(".")) {
            for (final File fileFound : sourceDirectory.listFiles()) {
                if (file.getName().endsWith(fileName)) {
                    foundFile = fileFound;
                    break;
                }
            }
        }
        if (!foundFile.exists()) {
            return null;
        }
        return foundFile;
    }
}
