package com.synopsys.integration.detect.util;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

public class DirectoryManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        Assert.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a"));
        Assert.assertEquals("a", DetectFileUtils.extractFinalPieceFromPath("/a/"));
        Assert.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c"));
        Assert.assertEquals("c", DetectFileUtils.extractFinalPieceFromPath("/a/b/c/"));
    }
}
