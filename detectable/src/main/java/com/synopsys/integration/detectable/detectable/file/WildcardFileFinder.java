/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
