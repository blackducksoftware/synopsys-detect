package com.synopsys.integration.detect.tool.binaryscanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryIncludedFileFilter;

public class DetectExcludedDirectoryIncludedFileFilterTest {

    @Test
    void testFileInclusion() {
        List<String> excludedDirs = Arrays.asList("excludeme");
        List<String> includedFiles = Arrays.asList("*.txt");
        DetectExcludedDirectoryIncludedFileFilter filter = new DetectExcludedDirectoryIncludedFileFilter(excludedDirs, includedFiles);

        assertTrue(filter.test(new File("/tmp/aaa/binarytest/includeme.txt")));
        assertTrue(filter.test(new File("/tmp/aaa/binarytest/dir/includeme2.txt")));

        assertFalse(filter.test(new File("/tmp/aaa/binarytest/dir/nonexistent.txt")));
        assertFalse(filter.test(new File("/tmp/aaa/binarytest/dir/nonmatching.jar")));
    }

    @Test
    void testDirExclusion() {
        List<String> excludedDirs = Arrays.asList("excludeme");
        List<String> includedFiles = Arrays.asList("*.txt");
        DetectExcludedDirectoryIncludedFileFilter filter = new DetectExcludedDirectoryIncludedFileFilter(excludedDirs, includedFiles);

        assertFalse(filter.isExcludedDirectory(new File("/tmp/aaa/binarytest/dir")));
        assertTrue(filter.isExcludedDirectory(new File("/tmp/aaa/binarytest/dir/excludeme")));
    }
}
