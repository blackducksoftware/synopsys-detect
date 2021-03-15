/**
 * detector
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
package com.synopsys.integration.detector.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorFinderTest {
    private static Path initialDirectoryPath;

    @BeforeAll
    public static void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("DetectorFinderTest");
    }

    @AfterAll
    public static void cleanup() {
        try {
            FileUtils.deleteDirectory(initialDirectoryPath.toFile());
        } catch (final IOException e) {
            // ignore
        }
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSimple() throws DetectorFinderDirectoryListException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "testSimple");
        subDir.mkdirs();

        final File subSubDir1 = new File(subDir, "subSubDir1");
        subSubDir1.mkdir();

        final File subSubDir2 = new File(subDir, "subSubDir2");
        subSubDir2.mkdir();

        final DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0), new HashMap<>());
        final Predicate<File> fileFilter = f -> true;
        final int maximumDepth = 10;
        final DetectorFinderOptions options = new DetectorFinderOptions(fileFilter, maximumDepth);

        final DetectorFinder finder = new DetectorFinder();
        final Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options, new SimpleFileFinder());

        // make sure both dirs were found
        final Set<DetectorEvaluationTree> testDirs = tree.get().getChildren();
        DetectorEvaluationTree simpleTestDir = null;
        for (final DetectorEvaluationTree testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSimple")) {
                simpleTestDir = testDir;
                break;
            }
        }
        final Set<DetectorEvaluationTree> subDirResults = simpleTestDir.getChildren();
        assertEquals(2, subDirResults.size());
        final String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertTrue(subDirContentsName.startsWith("subSubDir"));
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksNotFollowed() throws IOException, DetectorFinderDirectoryListException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        // Create a subDir with a symlink that loops back to its parent
        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "testSymLinksNotFollowed");
        subDir.mkdirs();
        final File link = new File(subDir, "linkToInitial");
        final Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        final File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();

        final DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0), new HashMap<>(0));
        final Predicate<File> fileFilter = f -> true;
        final int maximumDepth = 10;
        final DetectorFinderOptions options = new DetectorFinderOptions(fileFilter, maximumDepth);

        final DetectorFinder finder = new DetectorFinder();
        final Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options, new SimpleFileFinder());

        // make sure the symlink was omitted from results
        //        final Set<DetectorEvaluationTree> subDirResults = tree.get().getChildren().iterator().next().getChildren();
        final Set<DetectorEvaluationTree> testDirs = tree.get().getChildren();
        DetectorEvaluationTree symLinkTestDir = null;
        for (final DetectorEvaluationTree testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSymLinksNotFollowed")) {
                symLinkTestDir = testDir;
                break;
            }
        }
        final Set<DetectorEvaluationTree> subDirResults = symLinkTestDir.getChildren();

        assertEquals(1, subDirResults.size());
        final String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertEquals("regularDir", subDirContentsName);
    }

}
