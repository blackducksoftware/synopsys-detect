/*
 * detectable
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
package com.synopsys.integration.detectable.detectable.file;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;

public class WildcardFileFinder implements FileFinder {

    private List<File> findFiles(final File directoryToSearch, final FilenameFilter filenameFilter, final int depth, final boolean findInsideMatchingDirectories) {
        final List<File> foundFiles = new ArrayList<>();
        if (depth < 0) {
            return foundFiles;
        }
        if (Files.isSymbolicLink(directoryToSearch.toPath())) {
            return foundFiles;
        }
        final File[] allFiles = directoryToSearch.listFiles();
        if (allFiles == null) {
            return foundFiles;
        }
        for (final File file : allFiles) {
            final boolean matches = filenameFilter.accept(directoryToSearch, file.getName());
            if (matches) {
                foundFiles.add(file);
            }
            if (!matches || findInsideMatchingDirectories) {
                if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
                    foundFiles.addAll(findFiles(file, filenameFilter, depth - 1, findInsideMatchingDirectories));
                }
            }
        }

        return foundFiles;
    }

    @NotNull
    @Override
    public List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns, final int depth, final boolean findInsideMatchingDirectories) {
        return findFiles(directoryToSearch, new WildcardFileFilter(filenamePatterns), depth, findInsideMatchingDirectories);
    }
}
