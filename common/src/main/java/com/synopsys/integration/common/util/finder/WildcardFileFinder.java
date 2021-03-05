package com.synopsys.integration.common.util.finder;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.common.util.finder.FileFinder;

public class WildcardFileFinder implements FileFinder {
    private final Predicate<File> filter;

    public WildcardFileFinder(final Predicate<File> filter) {
        this.filter = filter;
    }

    public WildcardFileFinder() {
        // if no predicate is passed, just accept all files
        this.filter = file -> true;
    }

    @NotNull
    @Override
    public List<File> findFiles(final File directoryToSearch, final Predicate<File> filter, final int depth, final boolean findInsideMatchingDirectories) {
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
            final boolean matches = filter.test(file) && this.filter.test(file);
            if (matches) {
                foundFiles.add(file);
            }
            if (!matches || findInsideMatchingDirectories) {
                if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
                    foundFiles.addAll(findFiles(file, filter, depth - 1, findInsideMatchingDirectories));
                }
            }
        }

        return foundFiles;
    }

    public List<File> findFiles(final File directoryToSearch, final int depth, final boolean findInsideMatchingDirectories) {
        return findFiles(directoryToSearch, filter, depth, findInsideMatchingDirectories);
    }

    public List<File> findFiles(final File directoryToSearch) {
        return findFiles(directoryToSearch, filter);
    }

    @NotNull
    @Override
    public List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns, final int depth, final boolean findInsideMatchingDirectories) {
        Predicate<File> wildcardFilter = file -> {
            WildcardFileFilter filter = new WildcardFileFilter(filenamePatterns);
            return filter.accept(file);
        };
        return findFiles(directoryToSearch, wildcardFilter, depth, findInsideMatchingDirectories);
    }

}
