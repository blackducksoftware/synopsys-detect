package com.synopsys.integration.detect.workflow.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DetectFileFinderTest {
    private static File sourceDirectory;

    @BeforeAll
    public static void setup() throws IOException {
        sourceDirectory = Files.createTempDirectory("DetectFileFinderTest").toFile();

    }

    @AfterAll
    public static void cleanup() {

    }

    @Test
    public void testRecursiveFinderOnLoopingSymlinks() {

//        final DetectFileFinder finder = new DetectFileFinder();
//        final StringBuilder maxDepthHitMsgPattern;
//        final int maxDepth;
//        final String filenamePatterns;
//        finder.findAllFilesToDepth(sourceDirectory, maxDepthHitMsgPattern, maxDepth, filenamePatterns);

    }
}
