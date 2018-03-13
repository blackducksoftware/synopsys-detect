package com.blackducksoftware.integration.hub.detect.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectFileManager {
    private final Logger logger = LoggerFactory.getLogger(DetectFileManager.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private FileFinder fileFinder;

    private final Set<File> directoriesToCleanup = new LinkedHashSet<>();

    public void cleanupDirectories() {
        if (null != directoriesToCleanup && !directoriesToCleanup.isEmpty()) {
            for (final File directory : directoriesToCleanup) {
                FileUtils.deleteQuietly(directory);
            }
        }
    }

    public File createDirectory(final BomToolType bomToolType) {
        return createDirectory(bomToolType.toString().toLowerCase(), true);
    }

    public File createDirectory(final String directoryName) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, true);
    }

    public File createDirectory(final File directory, final String newDirectoryName) {
        return createDirectory(directory, newDirectoryName, true);
    }

    public File createDirectory(final String directoryName, final boolean allowDelete) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, allowDelete);
    }

    public File createDirectory(final File directory, final String newDirectoryName, final boolean allowDelete) {
        final File newDirectory = new File(directory, newDirectoryName);
        newDirectory.mkdir();
        if (detectConfiguration.getCleanupBomToolFiles() && allowDelete) {
            directoriesToCleanup.add(newDirectory);
        }

        return newDirectory;
    }

    public File createFile(final File directory, final String filename) {
        final File newFile = new File(directory, filename);
        if (detectConfiguration.getCleanupBomToolFiles()) {
            newFile.deleteOnExit();
        }

        return newFile;
    }

    public File createFile(final BomToolType bomToolType, final String filename) {
        final File directory = createDirectory(bomToolType);
        return createFile(directory, filename);
    }

    public File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }

    public File writeToFile(final File file, final String contents, final boolean overwrite) throws IOException {
        if (file == null || !file.isFile()) {
            return null;
        }
        if (overwrite) {
            file.delete();
        }
        if (file.exists()) {
            logger.info(String.format("%s exists and not being overwritten", file.getAbsolutePath()));
        } else {
            FileUtils.write(file, contents, StandardCharsets.UTF_8);
        }

        return file;
    }

    public String extractFinalPieceFromPath(final String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        final String normalizedPath = FilenameUtils.normalizeNoEndSeparator(path, true);
        return normalizedPath.substring(normalizedPath.lastIndexOf("/") + 1, normalizedPath.length());
    }

    public boolean directoryExists(final String sourcePath, final String relativePath) {
        final File sourceDirectory = new File(sourcePath);
        final File relativeDirectory = new File(sourceDirectory, relativePath);
        return relativeDirectory.isDirectory();
    }

    public boolean containsAllFiles(final String sourcePath, final String... filenamePatterns) {
        return fileFinder.containsAllFiles(sourcePath, filenamePatterns);
    }

    public boolean containsAllFilesToDepth(final String sourcePath, final int maxDepth, final String... filenamePatterns) {
        return fileFinder.containsAllFilesToDepth(sourcePath, maxDepth, filenamePatterns);
    }

    public File findFile(final String sourcePath, final String filenamePattern) {
        return fileFinder.findFile(sourcePath, filenamePattern);
    }

    public File findFile(final File sourceDirectory, final String filenamePattern) {
        return fileFinder.findFile(sourceDirectory, filenamePattern);
    }

    public File[] findFiles(final File sourceDirectory, final String filenamePattern) {
        return fileFinder.findFiles(sourceDirectory, filenamePattern);
    }

    public File[] findFilesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public File[] findFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findFilesToDepth(sourceDirectory, filenamePattern, maxDepth);
    }

    public File[] findDirectoriesContainingDirectoriesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findDirectoriesContainingDirectoriesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public File[] findDirectoriesContainingFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findDirectoriesContainingFilesToDepth(sourceDirectory, filenamePattern, maxDepth);
    }

}
