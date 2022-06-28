package com.synopsys.integration.detect.battery.util.assertions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.synopsys.integration.detect.battery.util.BatteryFiles;
import com.synopsys.integration.detect.battery.util.Bdio2Compare;
import com.synopsys.integration.detect.battery.util.BdioIssue;

public class BatteryBdioAssert {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String resourcePrefix;
    private final String testName;

    public BatteryBdioAssert(String testName, String resourcePrefix) {
        this.testName = testName;
        this.resourcePrefix = resourcePrefix;
    }

    public void assertBdio(File bdioDirectory, String bdioFileName, File compareDirectory) throws IOException, JSONException {
        File[] bdio = bdioDirectory.listFiles();
        Assertions.assertTrue(bdio != null && bdio.length > 0, "Bdio output files could not be found.");

        File expectedBdioFolder = BatteryFiles.asFile("/" + resourcePrefix + "/bdio");
        File expectedBdioFile = new File(expectedBdioFolder, bdioFileName);
        Assertions.assertTrue(expectedBdioFile.exists());

        File actualBdioFile = new File(bdioDirectory, bdioFileName);
        Assertions.assertTrue(actualBdioFile.exists());

        logger.info("***BDIO BATTERY TEST|" + testName + "|" + resourcePrefix + "***");

        File actualDirectory = new File(compareDirectory, "actual");
        File expectedDirectory = new File(compareDirectory, "expected");
        Assertions.assertTrue(actualDirectory.mkdirs());
        Assertions.assertTrue(expectedDirectory.mkdirs());

        ZipUtil.unpack(actualBdioFile, actualDirectory);
        ZipUtil.unpack(expectedBdioFile, expectedDirectory);

        List<BdioIssue> issues = Bdio2Compare.compare(actualDirectory, expectedDirectory);

        if (issues.size() > 0) {
            logger.error("=================");
            logger.error("BDIO Issues");
            logger.error("=================");
            issues.forEach(issue -> logger.error(issue.getIssue()));
            logger.error("=================");
        }

        Assertions.assertEquals(0, issues.size(), "The BDIO comparison failed, one or more issues were found, please check the logs.");
    }
}
