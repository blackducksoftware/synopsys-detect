/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.workflow.hub;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class ExclusionPatternCreator {
    private final Logger logger = LoggerFactory.getLogger(ExclusionPatternCreator.class);

    private final FileFinder fileFinder;
    private final File scanTarget;

    public ExclusionPatternCreator(final FileFinder fileFinder, final File scanTarget) {
        this.fileFinder = fileFinder;
        this.scanTarget = scanTarget;
    }

    public Set<String> determineExclusionPatterns(final String maxDepthHitMsg, final int maxDepth, final String... hubSignatureScannerExclusionNamePatterns) {
        if (null == hubSignatureScannerExclusionNamePatterns || hubSignatureScannerExclusionNamePatterns.length < 1 && scanTarget.isDirectory()) {
            return Collections.emptySet();
        }
        final Set<String> scanExclusionPatterns = new HashSet<>();
        try {
            final String scanTargetPath = scanTarget.getCanonicalPath();
            final List<File> matchingFiles = fileFinder.findFiles(scanTarget, Arrays.asList(hubSignatureScannerExclusionNamePatterns), maxDepth); //TODO: re-add the depth hit message creator?
            for (final File matchingFile : matchingFiles) {
                final String matchingFilePath = matchingFile.getCanonicalPath();
                final String scanExclusionPattern = createExclusionPatternFromPaths(scanTargetPath, matchingFilePath);
                scanExclusionPatterns.add(scanExclusionPattern);
            }
        } catch (final IOException e) {
            logger.warn("Problem encountered finding the exclusion patterns for the scanner. " + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
        return scanExclusionPatterns;
    }

    private String createExclusionPatternFromPaths(final String rootPath, final String targetPath) {
        String scanExclusionPattern = targetPath.replace(rootPath, "/");
        if (scanExclusionPattern.contains("\\\\")) {
            scanExclusionPattern = scanExclusionPattern.replace("\\\\", "/");
        }
        if (scanExclusionPattern.contains("\\")) {
            scanExclusionPattern = scanExclusionPattern.replace("\\", "/");
        }
        if (scanExclusionPattern.contains("//")) {
            scanExclusionPattern = scanExclusionPattern.replace("//", "/");
        }
        if (!scanExclusionPattern.endsWith("/")) {
            scanExclusionPattern = scanExclusionPattern + "/";
        }
        return scanExclusionPattern;
    }
}
