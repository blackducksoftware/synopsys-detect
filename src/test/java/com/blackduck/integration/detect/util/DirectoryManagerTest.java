package com.blackduck.integration.detect.util;

import com.blackduck.integration.detect.workflow.file.DetectFileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DirectoryManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        Assertions.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a"));
        Assertions.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a/"));
        Assertions.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c"));
        Assertions.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c/"));
    }
}
