package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatteryBdioAssert {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String resourcePrefix;
    private final String testName;

    public BatteryBdioAssert(final String testName, final String resourcePrefix) {
        this.testName = testName;
        this.resourcePrefix = resourcePrefix;
    }

    public void assertBdio(File bdioDirectory) throws IOException, JSONException, BdioCompare.BdioCompareException {
        File[] bdio = bdioDirectory.listFiles();
        Assertions.assertTrue(bdio != null && bdio.length > 0, "Bdio output files could not be found.");

        File expectedBdioFolder = BatteryFiles.asFile("/" + resourcePrefix + "/bdio");
        File[] expectedBdioFiles = expectedBdioFolder.listFiles();
        Assertions.assertTrue(expectedBdioFiles != null && expectedBdioFiles.length > 0, "Expected bdio resource files could not be found: " + expectedBdioFolder.getCanonicalPath());
        Assertions.assertEquals(expectedBdioFiles.length, bdio.length, "Detect did not create the expected number of bdio files.");

        List<File> actualByName = Arrays.stream(bdio).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        List<File> expectedByName = Arrays.stream(expectedBdioFiles).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

        int issueCount = 0;
        for (int i = 0; i < expectedByName.size(); i++) {
            logger.info("***BDIO BATTERY TEST|" + testName + "|" + resourcePrefix + "|" + expectedByName.get(i).getName() + "***");

            File expected = expectedByName.get(i);
            File actual = actualByName.get(i);
            Assertions.assertEquals(expected.getName(), actual.getName(), "Bdio file names did not match when sorted.");

            String expectedJson = FileUtils.readFileToString(expected, Charset.defaultCharset());
            String actualJson = FileUtils.readFileToString(actual, Charset.defaultCharset());

            JSONArray expectedJsonArray = (JSONArray) JSONParser.parseJSON(expectedJson);
            JSONArray actualJsonArray = (JSONArray) JSONParser.parseJSON(actualJson);

            BdioCompare compare = new BdioCompare();
            List<BdioCompare.BdioIssue> issues = compare.compare(expectedJsonArray, actualJsonArray);

            if (issues.size() > 0) {
                logger.error("=================");
                logger.error("BDIO Issues");
                logger.error("Expected: " + expected.getCanonicalPath());
                logger.error("Actual: " + actual.getCanonicalPath());
                logger.error("=================");
                issues.forEach(issue -> logger.error(issue.getIssue()));
                logger.error("=================");
            }
            issueCount += issues.size();
        }
        Assertions.assertEquals(0, issueCount, "The BDIO comparison failed, one or more issues were found, please check the logs.");
    }
}
