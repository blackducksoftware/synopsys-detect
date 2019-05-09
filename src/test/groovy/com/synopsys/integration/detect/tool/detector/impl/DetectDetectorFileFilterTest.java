package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class DetectDetectorFileFilterTest {
    @Test
    void testIsExcludedDirectories() {
        final Path sourcePath = new File("my/file/path").toPath();
        final List<String> excludedDirectories = Arrays.asList("root", "root2");
        final List<String> excludedDirectoryPaths = new ArrayList<>();
        final List<String> excludedDirectoryNamePatterns = new ArrayList<>();
        final DetectDetectorFileFilter detectDetectorFileFilter = new DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File(sourcePath.toFile(), "root");
        final File root2 = new File(sourcePath.toFile(), "root2");
        final File doNotExcludeDir = new File(root, "doNotExclude");

        Assert.assertTrue(detectDetectorFileFilter.isExcluded(root));
        Assert.assertTrue(detectDetectorFileFilter.isExcluded(root2));
        Assert.assertFalse(detectDetectorFileFilter.isExcluded(doNotExcludeDir));
    }

    @Test
    void testIsExcludedDirectoryPaths() {
        final Path sourcePath = new File("my/subDir1/subDir2/file/path").toPath();
        final List<String> excludedDirectories = new ArrayList<>();
        final List<String> excludedDirectoryPaths = Collections.singletonList("subDir1/subDir2");
        final List<String> excludedDirectoryNamePatterns = new ArrayList<>();
        final DetectDetectorFileFilter detectDetectorFileFilter = new DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File("path/to/root");
        final File subDir1 = new File(root, "subDir1");
        final File subDir2 = new File(root, "subDir2");
        final File deepSubDir2 = new File(subDir1, "subDir2");

        Assert.assertFalse(detectDetectorFileFilter.isExcluded(root));
        Assert.assertFalse(detectDetectorFileFilter.isExcluded(subDir1));
        Assert.assertFalse(detectDetectorFileFilter.isExcluded(subDir2));
        Assert.assertTrue(detectDetectorFileFilter.isExcluded(deepSubDir2));
    }

    @Test
    void testIsExcludedDirectoryNamePatterns() {
        final Path sourcePath = new File("my/subDir1/subDir2/file/path").toPath();
        final List<String> excludedDirectories = new ArrayList<>();
        final List<String> excludedDirectoryPaths = new ArrayList<>();
        final List<String> excludedDirectoryNamePatterns = Arrays.asList("*1", "namePatternsDir*");
        final DetectDetectorFileFilter detectDetectorFileFilter = new DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryNamePatterns);

        final File root = new File(sourcePath.toFile(), "root");
        final File subDir1 = new File(root, "subDir1");
        final File subDir2 = new File(root, "subDir2");
        final File deepSubDir2 = new File(subDir1, "subDir2");
        final File namePatternsDir = new File(root, "namePatternsDir51134");

        Assert.assertFalse(detectDetectorFileFilter.isExcluded(root));
        Assert.assertTrue(detectDetectorFileFilter.isExcluded(subDir1));
        Assert.assertFalse(detectDetectorFileFilter.isExcluded(subDir2));
        Assert.assertFalse(detectDetectorFileFilter.isExcluded(deepSubDir2));
        Assert.assertTrue(detectDetectorFileFilter.isExcluded(namePatternsDir));
    }
}