package com.synopsys.integration.detect.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

public class DirectoryManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        Assertions.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a"));
        Assertions.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a/"));
        Assertions.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c"));
        Assertions.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c/"));
    }
}
