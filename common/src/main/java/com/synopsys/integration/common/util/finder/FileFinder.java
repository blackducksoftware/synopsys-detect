package com.synopsys.integration.common.util.finder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileFinder {
    // Find with predicate
    @Nullable
    default File findFile(File directoryToSearch, Predicate<File> filter) {
        return findFile(directoryToSearch, filter, true, 0);
    }

    @Nullable
    default File findFile(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth) {
        List<File> files = findFiles(directoryToSearch, filter, followSymLinks, depth);
        if (CollectionUtils.isNotEmpty(files)) {
            return files.get(0);
        }
        return null;
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, Predicate<File> filter) {
        return findFiles(directoryToSearch, filter, true, 0);
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth) {
        return findFiles(directoryToSearch, filter, followSymLinks, depth, true);
    }

    @NotNull
    List<File> findFiles(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories);

    // Find with file pame patterns
    @Nullable
    default File findFile(File directoryToSearch, String filenamePattern) {
        return findFile(directoryToSearch, filenamePattern, true, 0);
    }

    @Nullable
    default File findFile(File directoryToSearch, String filenamePattern, boolean followSymLinks, int depth) {
        List<File> files = findFiles(directoryToSearch, Collections.singletonList(filenamePattern), followSymLinks, depth);
        if (CollectionUtils.isNotEmpty(files)) {
            return files.get(0);
        }
        return null;
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, String filenamePattern) {
        return findFiles(directoryToSearch, Collections.singletonList(filenamePattern), true, 0);
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, String filenamePattern, boolean followSymLinks, int depth) {
        return findFiles(directoryToSearch, Collections.singletonList(filenamePattern), followSymLinks, depth);
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, List<String> filenamePatterns) {
        return findFiles(directoryToSearch, filenamePatterns, true, 0);
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, List<String> filenamePatterns, boolean followSymLinks, int depth) {
        return findFiles(directoryToSearch, filenamePatterns, followSymLinks, depth, true);
    }

    @NotNull
    default List<File> findFiles(File directoryToSearch, List<String> filenamePatterns, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories) {
        Predicate<File> wildcardFilter = file -> {
            WildcardFileFilter filter = new WildcardFileFilter(filenamePatterns);
            return filter.accept(file);
        };
        return findFiles(directoryToSearch, wildcardFilter, followSymLinks, depth, findInsideMatchingDirectories);
    }

}
