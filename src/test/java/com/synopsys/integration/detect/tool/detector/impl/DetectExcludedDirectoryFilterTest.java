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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

        assertTrue(detectExcludedDirectoryFilter.isExcluded(root));
        assertTrue(detectExcludedDirectoryFilter.isExcluded(root2));
        assertFalse(detectExcludedDirectoryFilter.isExcluded(doNotExcludeDir));
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

        assertFalse(detectExcludedDirectoryFilter.isExcluded(root));
        assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir1));
        assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir2));
        assertTrue(detectExcludedDirectoryFilter.isExcluded(deepSubDir2));
    }

    @ParameterizedTest
    @MethodSource("inputPatternsToExclusionsProvider")
    void testIsExcludedDirectoryPathPatterns(String exclusionPattern, boolean sub1Excluded, boolean sub2Excluded, boolean sub1Sub1Excluded, boolean sub1Sub2Excluded, boolean sub2Sub1Excluded) {
        final Path sourcePath = new File("").toPath();
        final List<String> excludedDirectories = new ArrayList<>();

        final DetectExcludedDirectoryFilter filter = new DetectExcludedDirectoryFilter(sourcePath, excludedDirectories, Collections.singletonList(exclusionPattern), new LinkedList<>());
        final File root = new File("root");
        final File sub1 = new File(root, "sub1");
        final File sub2 = new File(root, "sub2");
        final File sub1Sub1 = new File(sub1, "sub1Sub1");
        final File sub1Sub2 = new File(sub1, "sub1Sub2");
        final File sub2Sub1 = new File(sub2, "sub2Sub1");

        assertEquals(sub1Excluded, filter.isExcluded(sub1));
        assertEquals(sub2Excluded, filter.isExcluded(sub2));
        assertEquals(sub1Sub1Excluded, filter.isExcluded(sub1Sub1));
        assertEquals(sub1Sub2Excluded, filter.isExcluded(sub1Sub2));
        assertEquals(sub2Sub1Excluded, filter.isExcluded(sub2Sub1));

    }

    static Stream<Arguments> inputPatternsToExclusionsProvider() {
        // Stream of single input patterns and booleans corresponding to the files created in testIsExcludedDirectoryPathPatterns (with the exception of root), whether or not they should be excluded according to the pattern
        return Stream.of(
            arguments("**ub2", false, true, false, true, false),
            arguments("**root/sub1/sub1*", false, false, true, true, false),
            arguments("**root/*/*", false, false, true, true, true)
        );
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

        assertFalse(detectExcludedDirectoryFilter.isExcluded(root));
        assertTrue(detectExcludedDirectoryFilter.isExcluded(subDir1));
        assertFalse(detectExcludedDirectoryFilter.isExcluded(subDir2));
        assertFalse(detectExcludedDirectoryFilter.isExcluded(deepSubDir2));
        assertTrue(detectExcludedDirectoryFilter.isExcluded(namePatternsDir));
    }
}