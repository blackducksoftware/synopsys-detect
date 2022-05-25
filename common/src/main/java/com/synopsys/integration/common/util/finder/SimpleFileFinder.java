package com.synopsys.integration.common.util.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;

public class SimpleFileFinder implements FileFinder {

    @NotNull
    @Override
    public List<File> findFiles(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories) {
        return findFiles(directoryToSearch, filter, followSymLinks, depth, findInsideMatchingDirectories, false);
    }

    @NotNull
    @Override
    public List<File> findFiles(File directoryToSearch, Predicate<File> filter, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories, boolean pruneNonMatchingDirectories) {
        List<File> foundFiles = new ArrayList<>();
        if (depth < 0) {
            return foundFiles;
        }
        if (!shouldFindinDirectory(directoryToSearch, followSymLinks)) {
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
            if (file.isDirectory() && !matches && pruneNonMatchingDirectories) {
                continue; // prune it
            }
            if (!matches || findInsideMatchingDirectories) {
                if (shouldFindinDirectory(file, followSymLinks)) {
                    foundFiles.addAll(findFiles(file, filter, followSymLinks, depth - 1, findInsideMatchingDirectories));
                }
            }
        }

        return foundFiles;
    }

    private boolean shouldFindinDirectory(File file, boolean followSymLinks) {
        return (file.isDirectory() && (!Files.isSymbolicLink(file.toPath()) || followSymLinks)) && linkPointsToValidDirectory(file);
    }

    private boolean linkPointsToValidDirectory(File directory) {
        Path linkTarget;
        try {
            linkTarget = directory.toPath().toRealPath();
        } catch (IOException e) {
            return false;
        }
        if (!Files.isDirectory(linkTarget)) {
            return false;
        }
        return true;
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
