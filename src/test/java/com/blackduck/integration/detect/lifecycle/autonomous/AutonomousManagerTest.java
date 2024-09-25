package com.blackduck.integration.detect.lifecycle.autonomous;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.blackduck.integration.detect.configuration.DetectProperties;
import com.blackduck.integration.detect.configuration.DetectPropertyConfiguration;
import com.blackduck.integration.detect.lifecycle.autonomous.AutonomousManager;
import com.blackduck.integration.detect.lifecycle.autonomous.model.PackageManagerType;
import com.blackduck.integration.detect.lifecycle.autonomous.model.ScanSettings;
import com.blackduck.integration.detect.lifecycle.autonomous.model.ScanType;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;

public class AutonomousManagerTest {

    private static final File PRE_RUN_SCAN_SETTINGS_TEST_DIR = new File("src/test/resources/lifecycle/autonomous/pre-run-scan-settings");
    private static final File POST_RUN_SCAN_SETTINGS_TEST_DIR = new File("src/test/resources/lifecycle/autonomous/post-run-scan-settings");
    private static boolean autonomousScanEnabled;
    private static SortedMap<String, String> userProvidedProperties;
    private static DirectoryManager directoryManagerMock;
    private static DetectPropertyConfiguration detectPropertyConfigurationMock;

    private static String TEST_DETECT_SOURCE_PATH = "/Users/username/detect";
    private static String TEST_HASHED_SCAN_SETTINGS_FILE_NAME = "9d6c66c9-8c9b-31ef-92e1-982c0dbef944.json";
    private static File postRunScanSettingsTestFile;

    public AutonomousManagerTest() {
        setUpMockDefaults();
    }

    public void setUpMockDefaults() {
        directoryManagerMock = Mockito.mock(DirectoryManager.class);
        detectPropertyConfigurationMock = Mockito.mock(DetectPropertyConfiguration.class);
        autonomousScanEnabled = true;
        userProvidedProperties = new TreeMap<>();
//        userProvidedProperties.put("detect.tools", "DETECTOR");
        userProvidedProperties.put("blackduck.url", "https://blackduck.test.url.com");
        userProvidedProperties.put("blackduck.api.token", "test-token");
        userProvidedProperties.put("detect.autonomous.scan.enabled", String.valueOf(autonomousScanEnabled));
        userProvidedProperties.put("detect.source.path", TEST_DETECT_SOURCE_PATH);
        userProvidedProperties.put("detect.cleanup", "true");
        userProvidedProperties.put("detect.binary.scan.search.depth", "10");

        Mockito.doReturn(new File(TEST_DETECT_SOURCE_PATH)).when(directoryManagerMock).getSourceDirectory();
        setUpNoScanSettingsMock();
    }

    private void setUpPreviousRunScanSettingsMock() {
        Mockito.doReturn(PRE_RUN_SCAN_SETTINGS_TEST_DIR).when(directoryManagerMock).getScanSettingsOutputDirectory();
    }

    public void setUpNoScanSettingsMock() {
        Mockito.doReturn(null).when(directoryManagerMock).getScanSettingsOutputDirectory();
    }

    private void setUpPostRunScanSettingsMock() {
        Mockito.doReturn(POST_RUN_SCAN_SETTINGS_TEST_DIR).when(directoryManagerMock).getScanSettingsOutputDirectory();
    }

    @Test
    public void testScanSettingsFileCreation() throws IOException {
        setUpPostRunScanSettingsMock();

        AutonomousManager autonomousManager = new AutonomousManager(directoryManagerMock, detectPropertyConfigurationMock, autonomousScanEnabled, userProvidedProperties);

        autonomousManager.writeScanSettingsModelToTarget();
        postRunScanSettingsTestFile = autonomousManager.getScanSettingsTargetFile();

        // Ensure file is created
        Assertions.assertTrue(postRunScanSettingsTestFile.exists());

        // Ensure hashed filename is correct
        Assertions.assertEquals(postRunScanSettingsTestFile.getPath(), String.join("/", POST_RUN_SCAN_SETTINGS_TEST_DIR.getPath(), TEST_HASHED_SCAN_SETTINGS_FILE_NAME));
    }


    @Test
    public void testNoPreviousScanSettingsFile() {
        setUpNoScanSettingsMock();
        AutonomousManager autonomousManager = new AutonomousManager(directoryManagerMock, detectPropertyConfigurationMock, autonomousScanEnabled, userProvidedProperties);

        // Ensure scan settings target file is null if no file was found in scan settings directory
        Assertions.assertFalse(autonomousManager.isScanSettingsFilePresent());

        // Ensure that the scan settings model is initialized as an empty model if no file was found in scan settings directory
        autonomousManager.initializeScanSettingsModel();
        Assertions.assertTrue(autonomousManager.getScanSettingsModel().isEmpty());
    }

    @Test
    public void testPreviousScanSettingsFileScenarios() {
        setUpPreviousRunScanSettingsMock();
        AutonomousManager autonomousManager = new AutonomousManager(directoryManagerMock, detectPropertyConfigurationMock, autonomousScanEnabled, userProvidedProperties);

        ScanSettings scanSettingsModel = autonomousManager.getScanSettingsModel();
        SortedMap<String, String> globalDetectProperties = scanSettingsModel.getGlobalDetectProperties();
        SortedSet<PackageManagerType> detectorTypes = scanSettingsModel.getDetectorTypes();
        SortedSet<ScanType> scanTypes = scanSettingsModel.getScanTypes();

        // Ensure scan settings target file exists if file was found in scan settings directory
        Assertions.assertTrue(autonomousManager.isScanSettingsFilePresent());

        // Ensure scan settings model is initialized with values from the file found in the local scan settings directory

        // Should not be empty
        Assertions.assertFalse(scanSettingsModel.isEmpty());

        // Ensure deserialization is accurate
        // Verify global property values
        Assertions.assertEquals(globalDetectProperties.get("blackduck.offline.mode"), "false");
        Assertions.assertEquals(globalDetectProperties.get("blackduck.trust.cert"), "false");
        Assertions.assertEquals(globalDetectProperties.get("detect.blackduck.scan.mode"), "INTELLIGENT");

        // Verify detector objects and their properties
        Assertions.assertEquals(detectorTypes.size(), 2);
        Assertions.assertEquals(scanTypes.size(), 3);

        PackageManagerType testDetector = detectorTypes.first();
        Assertions.assertEquals(testDetector.getDetectorTypeName(), "GRADLE");

        SortedMap<String, String> testDetectorProperties = testDetector.getDetectorProperties();
        Assertions.assertEquals(testDetectorProperties.get("detect.gradle.configuration.types.excluded"), "NONE");

        SortedSet<String> testDetectorScanTargets = testDetector.getDetectorScanTargets();
        Assertions.assertEquals(testDetectorScanTargets.size(), 7);
        Assertions.assertEquals(testDetectorScanTargets.first(), "/Users/username/project-name");

        ScanType testScanType = scanTypes.first();
        Assertions.assertEquals(testScanType.getScanTypeName(), "BINARY_SCAN");
        Assertions.assertEquals(testScanType.getScanProperties().get("detect.binary.scan.search.depth"), "4");
        Assertions.assertEquals(testScanType.getScanTargets().first(), "/Users/username/project-name/binary1.exe");
    }

    @Test
    public void testDeltaCheckingOperations() {
        setUpPreviousRunScanSettingsMock();
        AutonomousManager autonomousManager = new AutonomousManager(directoryManagerMock, detectPropertyConfigurationMock, autonomousScanEnabled, userProvidedProperties);

        SortedMap<String, String> defaultValueMap = DetectProperties.getDefaultValues();
        List<String> allPropertyKeys = DetectProperties.allProperties().getPropertyKeys();

        // Remove one property from all properties
        defaultValueMap.remove("detect.timeout");
        allPropertyKeys.remove("detect.timeout");

        // Only adopt DETECTOR and BINARY_SCAN - SIGNATURE_SCAN should not appear in the model
        Set<String> adoptedScanTypes = new HashSet<>(Arrays.asList("DETECTOR", "BINARY_SCAN"));

        // Only adopt GRADLE detector type, MAVEN should not appear in the model
        Set<String> adoptedDetectorTypes = new HashSet<>(Arrays.asList("GRADLE"));

        // Perform delta-checking operations and update allProperties in Autonomous Manager
        autonomousManager.updateScanSettingsProperties(defaultValueMap, adoptedScanTypes, adoptedDetectorTypes, allPropertyKeys);

        // Save the post-delta-checking results to the model
        autonomousManager.savePropertiesToModel();

        ScanSettings scanSettingsModel = autonomousManager.getScanSettingsModel();

        SortedMap<String, String> globalDetectProperties = scanSettingsModel.getGlobalDetectProperties();
        SortedSet<PackageManagerType> detectorTypes = scanSettingsModel.getDetectorTypes();
        SortedSet<ScanType> scanTypes = scanSettingsModel.getScanTypes();

        // Should not be empty
        Assertions.assertFalse(scanSettingsModel.isEmpty());

        // Verify updated global property values
        Assertions.assertEquals(globalDetectProperties.get("detect.cleanup"), "true");
        Assertions.assertEquals(globalDetectProperties.get("detect.timeout"), null);

        // Only GRADLE detector type should be present
        Assertions.assertEquals(detectorTypes.size(), 1);
        PackageManagerType testDetector = detectorTypes.first();
        Assertions.assertEquals(testDetector.getDetectorTypeName(), "GRADLE");

        // Only DETECTOR and BINARY_SCAN scan types should be present in the model
        Assertions.assertEquals(scanTypes.size(), 2);
        ScanType testScanType = scanTypes.first();
        Assertions.assertEquals(testScanType.getScanTypeName(), "BINARY_SCAN");
        Assertions.assertEquals(testScanType.getScanProperties().get("detect.binary.scan.search.depth"), "10");

        // Scan target values should be same as found in the previous file
        Assertions.assertEquals(testScanType.getScanTargets().first(), "/Users/username/project-name/binary1.exe");
    }

}
