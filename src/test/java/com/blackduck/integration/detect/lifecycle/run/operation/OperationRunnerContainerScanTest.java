package com.blackduck.integration.detect.lifecycle.run.operation;


import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.blackduck.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.testutils.ContainerScanTestUtils;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.util.NameVersion;

public class OperationRunnerContainerScanTest {
    private static final Gson gson = new Gson();
    private static final ContainerScanTestUtils containerScanTestUtils = new ContainerScanTestUtils();

    private File updateDetectConfigAndGetContainerImage(BlackduckScanMode blackduckScanMode, String imageFilePath)
        throws IntegrationException, IOException, DetectUserFriendlyException, OperationException {
        OperationRunner operationRunner = containerScanTestUtils.setUpDetectConfig(blackduckScanMode, imageFilePath);
        OperationRunner operationRunnerSpy = Mockito.spy(operationRunner);
        Mockito.doReturn(ContainerScanTestUtils.TEST_IMAGE_DOWNLOADED_FILE).when(operationRunnerSpy).downloadContainerImage(gson, ContainerScanTestUtils.TEST_DOWNLOAD_DIRECTORY, imageFilePath);
        return operationRunnerSpy.getContainerScanImage(gson, ContainerScanTestUtils.TEST_DOWNLOAD_DIRECTORY);
    }

    @Test
    public void testGetContainerScanImageForLocalFilePath() throws DetectUserFriendlyException, IntegrationException, IOException, OperationException {
        // Intelligent
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.INTELLIGENT, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(ContainerScanTestUtils.TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);

        // Stateless
        containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.STATELESS, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(ContainerScanTestUtils.TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
    }

    @Test
    public void testGetContainerScanImageForImageUrl() throws DetectUserFriendlyException, IntegrationException, IOException, OperationException {
        // Intelligent
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.INTELLIGENT, ContainerScanTestUtils.TEST_IMAGE_URL);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(ContainerScanTestUtils.TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);

        // Stateless
        containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.STATELESS, ContainerScanTestUtils.TEST_IMAGE_URL);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(ContainerScanTestUtils.TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
    }

    @Test
    public void testGetScanServiceDetailsForIntelligent() {
        OperationRunner operationRunner = containerScanTestUtils.setUpDetectConfig(BlackduckScanMode.INTELLIGENT, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);

        // Test if the correct endpoint is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostEndpoint().isEmpty());
        Assertions.assertEquals(ContainerScanTestUtils.INTELLIGENT_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());
        Assertions.assertNotEquals(ContainerScanTestUtils.RAPID_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());

        // Test if the correct content type is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostContentType().isEmpty());
        Assertions.assertEquals(ContainerScanTestUtils.INTELLIGENT_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
        Assertions.assertNotEquals(ContainerScanTestUtils.RAPID_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
    }

    @Test
    public void testGetScanServiceDetailsForStateless() {
        OperationRunner operationRunner = containerScanTestUtils.setUpDetectConfig(BlackduckScanMode.STATELESS, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);

        // Test if the correct endpoint is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostEndpoint().isEmpty());
        Assertions.assertEquals(ContainerScanTestUtils.RAPID_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());
        Assertions.assertNotEquals(ContainerScanTestUtils.INTELLIGENT_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());

        // Test if the correct content type is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostContentType().isEmpty());
        Assertions.assertEquals(ContainerScanTestUtils.RAPID_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
        Assertions.assertNotEquals(ContainerScanTestUtils.INTELLIGENT_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
    }

    @Test void testCreateContainerScanMetadataForIntelligent() {
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.INTELLIGENT;
        NameVersion projectNameVersion = new NameVersion(ContainerScanTestUtils.TEST_PROJECT_NAME, ContainerScanTestUtils.TEST_PROJECT_VERSION);
        OperationRunner operationRunner = containerScanTestUtils.setUpDetectConfig(blackduckScanMode, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);

        JsonObject expectedImageMetadataObject = new JsonObject();
        expectedImageMetadataObject.addProperty("scanId", ContainerScanTestUtils.TEST_SCAN_ID.toString());
        expectedImageMetadataObject.addProperty("scanType", ContainerScanTestUtils.SCAN_TYPE);
        expectedImageMetadataObject.addProperty("scanPersistence", ContainerScanTestUtils.SCAN_PERSISTENCE_TYPE_FOR_INTELLIGENT);
        expectedImageMetadataObject.addProperty("projectName", ContainerScanTestUtils.TEST_PROJECT_NAME);
        expectedImageMetadataObject.addProperty("projectVersionName", ContainerScanTestUtils.TEST_PROJECT_VERSION);
        expectedImageMetadataObject.addProperty("projectGroupName", ContainerScanTestUtils.TEST_PROJECT_GROUP);

        Assertions.assertFalse(expectedImageMetadataObject.isJsonNull());
        Assertions.assertTrue(expectedImageMetadataObject.isJsonObject());
        Assertions.assertEquals(expectedImageMetadataObject, operationRunner.createScanMetadata(ContainerScanTestUtils.TEST_SCAN_ID, projectNameVersion, "CONTAINER"));
    }

    @Test void testCreateContainerScanMetadataForStateless() {
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.STATELESS;
        NameVersion projectNameVersion = new NameVersion(ContainerScanTestUtils.TEST_PROJECT_NAME, ContainerScanTestUtils.TEST_PROJECT_VERSION);
        OperationRunner operationRunner = containerScanTestUtils.setUpDetectConfig(blackduckScanMode, ContainerScanTestUtils.TEST_IMAGE_LOCAL_FILE_PATH);

        JsonObject expectedImageMetadataObject = new JsonObject();
        expectedImageMetadataObject.addProperty("scanId", ContainerScanTestUtils.TEST_SCAN_ID.toString());
        expectedImageMetadataObject.addProperty("scanType", ContainerScanTestUtils.SCAN_TYPE);
        expectedImageMetadataObject.addProperty("scanPersistence", ContainerScanTestUtils.SCAN_PERSISTENCE_TYPE_FOR_STATELESS);
        expectedImageMetadataObject.addProperty("projectName", ContainerScanTestUtils.TEST_PROJECT_NAME);
        expectedImageMetadataObject.addProperty("projectVersionName", ContainerScanTestUtils.TEST_PROJECT_VERSION);
        expectedImageMetadataObject.addProperty("projectGroupName", ContainerScanTestUtils.TEST_PROJECT_GROUP);

        Assertions.assertFalse(expectedImageMetadataObject.isJsonNull());
        Assertions.assertTrue(expectedImageMetadataObject.isJsonObject());
        Assertions.assertEquals(expectedImageMetadataObject, operationRunner.createScanMetadata(ContainerScanTestUtils.TEST_SCAN_ID, projectNameVersion, "CONTAINER"));
    }

}
