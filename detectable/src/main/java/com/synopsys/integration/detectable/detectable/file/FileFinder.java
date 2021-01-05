/**
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
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileFinder {
    @Nullable
    default File findFile(final File directoryToSearch, final String filenamePattern) {
        return findFile(directoryToSearch, filenamePattern, 0);
    }

    @Nullable
    default File findFile(final File directoryToSearch, final String filenamePattern, final int depth) {
        final List<File> files = findFiles(directoryToSearch, Collections.singletonList(filenamePattern), depth);
        if (files != null && files.size() > 0) {
            return files.get(0);
        }
        return null;
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final String filenamePattern) {
        return findFiles(directoryToSearch, Collections.singletonList(filenamePattern), 0);
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final String filenamePattern, final int depth) {
        return findFiles(directoryToSearch, Collections.singletonList(filenamePattern), depth);
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns) {
        return findFiles(directoryToSearch, filenamePatterns, 0);
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns, final int depth) {
        return findFiles(directoryToSearch, filenamePatterns, depth, true);
    }

    @NotNull
    List<File> findFiles(File directoryToSearch, List<String> filenamePatterns, int depth, boolean findInsideMatchingDirectories);

}
