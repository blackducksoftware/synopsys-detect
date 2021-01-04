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
package com.synopsys.integration.detect.tool.detector.file;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectDetectorFileFilter implements Predicate<File> {
    private final Path sourcePath;
    private final List<String> excludedDirectories;
    private final List<String> excludedDirectoryPaths;
    private final WildcardFileFilter fileFilter;

    public DetectDetectorFileFilter(final Path sourcePath, final List<String> excludedDirectories, final List<String> excludedDirectoryPaths, final List<String> excludedDirectoryNamePatterns) {
        this.sourcePath = sourcePath;
        this.excludedDirectories = excludedDirectories;
        this.excludedDirectoryPaths = excludedDirectoryPaths;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean test(final File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(final File file) {
        for (final String excludedDirectory : excludedDirectories) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        for (final String excludedDirectory : excludedDirectoryPaths) {
            final Path excludedDirectoryPath = new File(excludedDirectory).toPath();
            final Path relativeDirectoryPath = sourcePath.relativize(file.toPath());
            if (relativeDirectoryPath.endsWith(excludedDirectoryPath)) {
                return true;
            }
        }

        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }
}
