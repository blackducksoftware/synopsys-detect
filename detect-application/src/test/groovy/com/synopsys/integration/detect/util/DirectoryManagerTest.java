package com.synopsys.integration.detect.util;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.workflow.file.DetectFileFinder;

public class DirectoryManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        final DetectFileFinder detectFileManager = new DetectFileFinder();
        Assert.assertEquals("a", detectFileManager.extractFinalPieceFromPath("/a"));
        Assert.assertEquals("a", detectFileManager.extractFinalPieceFromPath("/a/"));
        Assert.assertEquals("c", detectFileManager.extractFinalPieceFromPath("/a/b/c"));
        Assert.assertEquals("c", detectFileManager.extractFinalPieceFromPath("/a/b/c/"));
    }
}
