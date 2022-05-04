package com.synopsys.integration.detect.battery.util;

import static com.synopsys.integration.detect.battery.util.BatteryFiles.UTIL_RESOURCE_PREFIX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.zeroturnaround.zip.ZipUtil;

public class Bdio2CompareTests {
    public List<BdioIssue> testSimple(Path tempOutputDirectory, String actual, String expected) throws IOException, JSONException {
        File actualDirectory = new File(tempOutputDirectory.toFile(), "actual");
        File expectedDirectory = new File(tempOutputDirectory.toFile(), "expected");
        Assertions.assertTrue(actualDirectory.mkdirs());
        Assertions.assertTrue(expectedDirectory.mkdirs());

        File actualBdioFile = BatteryFiles.asFile("/bdio-compare/" + actual + ".bdio", UTIL_RESOURCE_PREFIX);
        File expectedBdioFile = BatteryFiles.asFile("/bdio-compare/" + expected + ".bdio", UTIL_RESOURCE_PREFIX);

        ZipUtil.unpack(actualBdioFile, actualDirectory);
        ZipUtil.unpack(expectedBdioFile, expectedDirectory);

        return Bdio2Compare.compare(actualDirectory, expectedDirectory);
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void testSimple(@TempDirectory.TempDir Path tempOutputDirectory) throws IOException, JSONException {
        List<BdioIssue> issues = testSimple(tempOutputDirectory, "base", "missing_component");
        Assertions.assertEquals(1, issues.size());
        Assertions.assertTrue(issues.get(0).getIssue().contains("SwiftyJSON"));
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void actualMissingRelationship(@TempDirectory.TempDir Path tempOutputDirectory) throws IOException, JSONException {
        List<BdioIssue> issues = testSimple(tempOutputDirectory, "base", "missing_relationship");
        Assertions.assertEquals(1, issues.size());
        Assertions.assertTrue(issues.get(0).getIssue().contains("SwiftHTTP"));
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void identicalMatches(@TempDirectory.TempDir Path tempOutputDirectory) throws IOException, JSONException {
        List<BdioIssue> issues = testSimple(tempOutputDirectory, "base", "base");
        Assertions.assertEquals(0, issues.size());
    }
}


