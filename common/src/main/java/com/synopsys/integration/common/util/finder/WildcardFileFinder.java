/*
 * common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.common.util.finder;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;

public class WildcardFileFinder implements FileFinder {
    @NotNull
    @Override
    public List<File> findFiles(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories) {
        List<File> foundFiles = new ArrayList<>();
        if (depth < 0) {
            return foundFiles;
        }
        if (Files.isSymbolicLink(directoryToSearch.toPath())) {
            return foundFiles;
        }
        File[] allFiles = directoryToSearch.listFiles();
        if (allFiles == null) {
            return foundFiles;
        }
        for (File file : allFiles) {
            boolean matches = filter.test(file);
            if (matches) {
                foundFiles.add(file);
            }
            if (!matches || findInsideMatchingDirectories) {
                if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
                    foundFiles.addAll(findFiles(file, filter, followSymLinks, depth - 1, findInsideMatchingDirectories));
                }
            }
        }

        return foundFiles;
    }

    @NotNull
    @Override
    public List<File> findFiles(File directoryToSearch, List<String> filenamePatterns, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories) {
        Predicate<File> wildcardFilter = file -> {
            WildcardFileFilter filter = new WildcardFileFilter(filenamePatterns);
            return filter.accept(file);
        };
        return findFiles(directoryToSearch, wildcardFilter, followSymLinks, depth, findInsideMatchingDirectories);
    }

}
