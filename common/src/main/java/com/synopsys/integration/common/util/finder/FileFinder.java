/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.common.util.finder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileFinder {
    // Find with predicate
    @Nullable
    default File findFile(final File directoryToSearch, final Predicate<File> filter) {
        return findFile(directoryToSearch, filter, 0);
    }

    @Nullable
    default File findFile(final File directoryToSearch, final Predicate<File> filter, final int depth) {
        final List<File> files = findFiles(directoryToSearch, filter, depth);
        if (files != null && files.size() > 0) {
            return files.get(0);
        }
        return null;
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final Predicate<File> filter) {
        return findFiles(directoryToSearch, filter, 0);
    }

    @NotNull
    default List<File> findFiles(final File directoryToSearch, final Predicate<File> filter, final int depth) {
        return findFiles(directoryToSearch, filter, depth, true);
    }

    @NotNull
    List<File> findFiles(File directoryToSearch, final Predicate<File> filter, int depth, boolean findInsideMatchingDirectories);

    // Find with file pame patterns
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
    List<File> findFiles(File directoryToSearch, final List<String> filenamePatterns, int depth, boolean findInsideMatchingDirectories);

}
