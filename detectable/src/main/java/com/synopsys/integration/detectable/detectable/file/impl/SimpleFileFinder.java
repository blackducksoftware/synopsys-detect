/**
 * detectable
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
package com.synopsys.integration.detectable.detectable.file.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class SimpleFileFinder implements FileFinder {

    @Override
    @Nullable
    public File findFile(final File directoryToSearch, final String filenamePattern) {
        return findFiles(directoryToSearch, filenamePattern).stream().findFirst().orElse(null);
    }

    @Override
    @NotNull
    public List<File> findFiles(final File directoryToSearch, final String filenamePattern) {
        return findFiles(directoryToSearch, filenamePattern, 0);
    }

    @Override
    public List<File> findFiles(final File directoryToSearch, final String filenamePattern, final int depth) {
        final FilenameFilter filter = new WildcardFileFilter(filenamePattern);
        return findFiles(directoryToSearch, filter, depth);
    }

    private List<File> findFiles(final File directoryToSearch, final FilenameFilter filenameFilter, final int depth) {
        final File[] foundFiles = directoryToSearch.listFiles(filenameFilter);

        if (foundFiles == null || foundFiles.length == 0) {
            return Collections.emptyList();
        }

        final List<File> aggregatedFiles = new ArrayList<>(Arrays.asList(foundFiles));

        final File[] allFiles = directoryToSearch.listFiles();
        if (allFiles != null && depth > 0) {
            final List<File> subFiles = Arrays.stream(allFiles)
                                            .filter(File::isDirectory)
                                            .flatMap(file -> findFiles(file, filenameFilter, depth - 1).stream())
                                            .collect(Collectors.toList());
            aggregatedFiles.addAll(subFiles);
        }

        return aggregatedFiles;
    }
}
