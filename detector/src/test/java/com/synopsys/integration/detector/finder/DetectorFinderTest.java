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
import org.jetbrains.annotations.NotNull;
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
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSimple() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, "testSimple");
        subDir.mkdirs();

        File subSubDir1 = new File(subDir, "subSubDir1");
        subSubDir1.mkdir();

        File subSubDir2 = new File(subDir, "subSubDir2");
        subSubDir2.mkdir();

        DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0));
        Predicate<File> fileFilter = f -> true;
        int maximumDepth = 10;
        DetectorFinderOptions options = new DetectorFinderOptions(fileFilter, maximumDepth, false);

        DetectorFinder finder = new DetectorFinder();
        Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options, new SimpleFileFinder());

        // make sure both dirs were found
        Set<DetectorEvaluationTree> testDirs = tree.get().getChildren();
        DetectorEvaluationTree simpleTestDir = null;
        for (DetectorEvaluationTree testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSimple")) {
                simpleTestDir = testDir;
                break;
            }
        }
        Set<DetectorEvaluationTree> subDirResults = simpleTestDir.getChildren();
        assertEquals(2, subDirResults.size());
        String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertTrue(subDirContentsName.startsWith("subSubDir"));
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksNotFollowed() throws IOException {
        testSymLinks(false);
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksFollowed() throws IOException {
        testSymLinks(true);
    }

    private void testSymLinks(boolean followSymLinks) throws IOException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File initialDirectory = createDirWithSymLink("testSymLinks");

        DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0));
        DetectorFinderOptions options = createFinderOptions(followSymLinks);

        DetectorFinder finder = new DetectorFinder();
        Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options, new SimpleFileFinder());

        // make sure the symlink was omitted from results
        //        final Set<DetectorEvaluationTree> subDirResults = tree.get().getChildren().iterator().next().getChildren();
        Set<DetectorEvaluationTree> testDirs = tree.get().getChildren();
        DetectorEvaluationTree symLinkTestDir = null;
        for (DetectorEvaluationTree testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSymLinks")) {
                symLinkTestDir = testDir;
                break;
            }
        }
        Set<DetectorEvaluationTree> subDirResults = symLinkTestDir.getChildren();

        if (followSymLinks) {
            assertEquals(2, subDirResults.size());
        } else {
            assertEquals(1, subDirResults.size());
            String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
            assertEquals("regularDir", subDirContentsName);
        }

        FileUtils.deleteDirectory(initialDirectory);
    }

    @NotNull
    private DetectorFinderOptions createFinderOptions(boolean followSymLinks) {
        Predicate<File> fileFilter = f -> true;
        int maximumDepth = 10;
        return new DetectorFinderOptions(fileFilter, maximumDepth, followSymLinks);
    }

    @NotNull
    private File createDirWithSymLink(String dirName) throws IOException {
        // Create a subDir with a symlink that loops back to its parent
        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, dirName);
        subDir.mkdirs();
        File link = new File(subDir, "linkToInitial");
        Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();
        return initialDirectory;
    }

}
