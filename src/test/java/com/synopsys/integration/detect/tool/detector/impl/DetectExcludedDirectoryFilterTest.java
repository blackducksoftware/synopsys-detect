/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;

class DetectExcludedDirectoryFilterTest {
    @Test
    void testIsExcludedDirectories() {
        final Path sourcePath = new File("my/file/path").toPath();
        final List<String> excludedDirectories = Arrays.asList("root", "root2");
        final List<String> excludedDirectoryPaths = new ArrayList<>();
        final List<String> excludedDirectoryNamePatterns = new ArrayList<>();
        final DetectExcludedDirectoryFilter detectExcludedDirectoryFilter = new DetectExcludedDirectoryFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File(sourcePath.toFile(), "root");
        final File root2 = new File(sourcePath.toFile(), "root2");
        final File doNotExcludeDir = new File(root, "doNotExclude");

        Assertions.assertTrue(detectExcludedDirectoryFilter.isExcluded(root));
        Assertions.assertTrue(detectExcludedDirectoryFilter.isExcluded(root2));
        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(doNotExcludeDir));
    }

    @Test
    void testIsExcludedDirectoryPaths() {
        final Path sourcePath = new File("my/subDir1/subDir2/file/path").toPath();
        final List<String> excludedDirectories = new ArrayList<>();
        final List<String> excludedDirectoryPaths = Collections.singletonList("subDir1/subDir2");
        final List<String> excludedDirectoryNamePatterns = new ArrayList<>();
        final DetectExcludedDirectoryFilter detectExcludedDirectoryFilter = new DetectExcludedDirectoryFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File("path/to/root");
        final File subDir1 = new File(root, "subDir1");
        final File subDir2 = new File(root, "subDir2");
        final File deepSubDir2 = new File(subDir1, "subDir2");

        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(root));
        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir1));
        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir2));
        Assertions.assertTrue(detectExcludedDirectoryFilter.isExcluded(deepSubDir2));
    }

    @Test
    void testIsExcludedDirectoryNamePatterns() {
        final Path sourcePath = new File("my/subDir1/subDir2/file/path").toPath();
        final List<String> excludedDirectories = new ArrayList<>();
        final List<String> excludedDirectoryPaths = new ArrayList<>();
        final List<String> excludedDirectoryNamePatterns = Arrays.asList("*1", "namePatternsDir*");
        final DetectExcludedDirectoryFilter detectExcludedDirectoryFilter = new DetectExcludedDirectoryFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File(sourcePath.toFile(), "root");
        final File subDir1 = new File(root, "subDir1");
        final File subDir2 = new File(root, "subDir2");
        final File deepSubDir2 = new File(subDir1, "subDir2");
        final File namePatternsDir = new File(root, "namePatternsDir51134");

        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(root));
        Assertions.assertTrue(detectExcludedDirectoryFilter.isExcluded(subDir1));
        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir2));
        Assertions.assertFalse(detectExcludedDirectoryFilter.isExcluded(deepSubDir2));
        Assertions.assertTrue(detectExcludedDirectoryFilter.isExcluded(namePatternsDir));
    }
}