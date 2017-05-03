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

    public boolean containsFiles(final String sourcePath, final PackageManagerFile... files) {
        return containsFiles(sourcePath, Arrays.asList(files));
    }

    public boolean containsFiles(final String sourcePath, final List<PackageManagerFile> files) {
        if (StringUtils.isBlank(sourcePath)) {
            return false;
        }

        final File sourceDirectory = new File(sourcePath);
        boolean containsFiles = false;
        if (sourceDirectory.isDirectory()) {
            for (final PackageManagerFile packageManagerFile : files) {
                final String fileName = packageManagerFile.fileName;
                final File file = findFile(sourceDirectory.getAbsolutePath(), packageManagerFile);
                if (file != null) {
                    containsFiles = true;
                } else if (packageManagerFile.mandatory) {
                    containsFiles = false;
                    logger.debug("Couldn't find a neccesary file >" + fileName);
                    break;
                }
            }
        }
        return containsFiles;
    }

    public File findFile(final String sourcePath, final PackageManagerFile packageManagerFile) {
        final File sourceDirectory = new File(sourcePath);
        final String fileName = packageManagerFile.fileName;
        File file = new File(sourceDirectory, fileName);
        if (fileName.startsWith(".")) {
            for (final File fileFound : sourceDirectory.listFiles()) {
                if (file.getName().endsWith(fileName)) {
                    file = fileFound;
                    break;
                }
            }
        }
        if (!file.exists()) {
            return null;
        }
        return file;
    }
}
