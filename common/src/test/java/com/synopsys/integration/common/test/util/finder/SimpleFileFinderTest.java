/**
 * detectable
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
package com.synopsys.integration.common.test.util.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;

public class SimpleFileFinderTest {

    private static Path initialDirectoryPath;

    @BeforeEach
    public void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("SimpleFileFinderTest");
    }

    @AfterEach
    public void cleanup() throws IOException {
        try {
            Files.delete(initialDirectoryPath);
        } catch (DirectoryNotEmptyException e) {
            FileUtils.deleteDirectory(initialDirectoryPath.toFile());

        }
    }

    // TODO need a test for symlinks ARE followed

    @Test
    @DisabledOnOs(WINDOWS)
    public void testSymlinksNotFollowed() throws IOException {
        // Create a subDir with a symlink that loops back to its parent
        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, "sub");
        subDir.mkdirs();
        File link = new File(subDir, "linkToInitial");
        Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();
        File regularFile = new File(subDir, "regularFile");
        regularFile.createNewFile();

        SimpleFileFinder finder = new SimpleFileFinder();
        List<String> filenamePatterns = Arrays.asList("sub", "linkToInitial", "regularDir", "regularFile");
        List<File> foundFiles = finder.findFiles(initialDirectoryPath.toFile(), filenamePatterns, false, 10);

        // make sure symlink not followed during dir traversal
        assertEquals(4, foundFiles.size());
    }

    @Test
    public void testFindWithPredicate() throws IOException {
        File initialDirectory = initialDirectoryPath.toFile();

        File subDir1 = new File(initialDirectory, "sub1");
        subDir1.mkdirs();
        File subDirChild1 = new File(subDir1, "child");
        subDirChild1.createNewFile();

        File subDir2 = new File(initialDirectory, "sub2");
        subDir2.mkdirs();
        File subDirChild2 = new File(subDir2, "child");
        subDirChild2.createNewFile();

        SimpleFileFinder fileFinder = new SimpleFileFinder();
        Predicate<File> filter = file -> file.getName().startsWith("sub");
        List<File> foundFiles = fileFinder.findFiles(initialDirectoryPath.toFile(), filter, false, 10);

        assertEquals(2, foundFiles.size());
        assertFalse(foundFiles.contains(subDirChild2));
    }
}
