package com.synopsys.integration.detect.lifecycle.autonomous;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class AutonomousManagerTest {

    private static final File PRE_RUN_SCAN_SETTINGS_TEST_DIR = new File("src/test/resources/lifecycle/autonomous/pre-run-scan-settings");
    private static final File POST_RUN_SCAN_SETTINGS_TEST_DIR = new File("src/test/resources/lifecycle/autonomous/post-run-scan-settings");
    private AutonomousManager autonomousManager;
    private AutonomousManager autonomousManagerSpy;
    private DirectoryManager directoryManagerMock;
    private DetectPropertyConfiguration detectPropertyConfigurationMock;

    private String TEST_DETECT_SOURCE_PATH = "/Users/username/synopsys-detect";
    private String TEST_HASHED_SCAN_SETTINGS_FILE_NAME = "9d6c66c9-8c9b-31ef-92e1-982c0dbef944.json";
    private File postRunScanSettingsTestFile;

    private void setUpMocks() {
        directoryManagerMock = Mockito.mock(DirectoryManager.class);
        detectPropertyConfigurationMock = Mockito.mock(DetectPropertyConfiguration.class);

        //        SortedMap<String, String> userProvidedProperties = new TreeMap<>();
        //        userProvidedProperties.put("detect.tools", "DETECTOR");
        //        userProvidedProperties.put("blackduck.url", "https://blackduck.test.url.com");
        //        userProvidedProperties.put("blackduck.api.token", "test-token");
        //        userProvidedProperties.put("detect.autonomous.scan.enabled", String.valueOf(autonomousScanEnabled));
        //        userProvidedProperties.put("detect.source.path", TEST_DETECT_SOURCE_PATH);
    }

    @Test
    public void testScanSettingsFileCreation() throws IOException {
        setUpMocks();

        boolean autonomousScanEnabled = true;
        SortedMap<String, String> userProvidedProperties = new TreeMap<>();
        Mockito.doReturn(new File(TEST_DETECT_SOURCE_PATH)).when(directoryManagerMock).getSourceDirectory();
        Mockito.doReturn(POST_RUN_SCAN_SETTINGS_TEST_DIR).when(directoryManagerMock).getScanSettingsOutputDirectory();
        // scanSettingsTargetFile will be null (test case: no previous scan settings file exists)

        autonomousManager = new AutonomousManager(directoryManagerMock, detectPropertyConfigurationMock, autonomousScanEnabled, userProvidedProperties);
        autonomousManagerSpy = Mockito.spy(autonomousManager);

        autonomousManagerSpy.writeScanSettingsModelToTarget();

        postRunScanSettingsTestFile = autonomousManagerSpy.getScanSettingsTargetFile();

        // Ensure file is created
        Assertions.assertTrue(postRunScanSettingsTestFile.exists());

        // Ensure hashed filename is correct
        Assertions.assertEquals(postRunScanSettingsTestFile.getPath(), String.join("/", POST_RUN_SCAN_SETTINGS_TEST_DIR.getPath(), TEST_HASHED_SCAN_SETTINGS_FILE_NAME));
    }
}
