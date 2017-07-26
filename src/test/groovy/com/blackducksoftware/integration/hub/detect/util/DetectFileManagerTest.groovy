package com.blackducksoftware.integration.hub.detect.util

import org.junit.Assert
import org.junit.Test

class DetectFileManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        def detectFileManager = new DetectFileManager()
        Assert.assertEquals('a', detectFileManager.extractFinalPieceFromPath('/a'))
        Assert.assertEquals('a', detectFileManager.extractFinalPieceFromPath('/a/'))
        Assert.assertEquals('c', detectFileManager.extractFinalPieceFromPath('/a/b/c'))
        Assert.assertEquals('c', detectFileManager.extractFinalPieceFromPath('/a/b/c/'))
    }
}
