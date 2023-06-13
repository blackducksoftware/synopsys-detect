package com.synopsys.integration.detect.lifecycle.run.operation;

import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupFindResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class OperationRunnerContainerScanTest {
    private static final Gson gson = new Gson();
    private static final String SCAN_TYPE = "CONTAINER";
    private static final String SCAN_PERSISTENCE_TYPE_FOR_STATELESS = "STATELESS";
    private static final String SCAN_PERSISTENCE_TYPE_FOR_INTELLIGENT = "STATEFUL";
    private static final String TEST_IMAGE_LOCAL_FILE_PATH = "src/test/resources/tool/container.scan/testImage.tar";
    private static final String TEST_IMAGE_URL = "https://www.container.artifactory.com/testImage.tar";
    private static final File TEST_IMAGE_LOCAL_FILE = new File("src/test/resources/tool/container.scan/testImage.tar");
    private static final File TEST_IMAGE_DOWNLOADED_FILE = new File("src/test/resources/tool/container.scan/testImageDownloaded.tar");
    private static final String RAPID_SCAN_ENDPOINT = "/api/developer-scans";
    private static final String RAPID_SCAN_CONTENT_TYPE = "application/vnd.blackducksoftware.scan-evidence-1+protobuf";
    private static final String INTELLIGENT_SCAN_ENDPOINT = "/api/intelligent-persistence-scans";
    private static final String INTELLIGENT_SCAN_CONTENT_TYPE = "application/vnd.blackducksoftware.intelligent-persistence-scan-3+protobuf";
    private static final String TEST_PROJECT_GROUP = "DEFAULT_PROJECT_GROUP";
    private static final String TEST_PROJECT_NAME = "DEFAULT_PROJECT_NAME";
    private static final String TEST_PROJECT_VERSION = "DEFAULT_PROJECT_VERSION";
    private static final UUID TEST_SCAN_ID = UUID.randomUUID();

    private DetectConfigurationFactory makeContainerScanFactory(BlackduckScanMode blackduckScanMode, String imageFilePath) {
        return factoryOf(
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, blackduckScanMode.toString()),
            Pair.of(DetectProperties.DETECT_TOOLS, DetectTool.CONTAINER_SCAN.toString()),
            Pair.of(DetectProperties.DETECT_PROJECT_NAME, TEST_PROJECT_NAME),
            Pair.of(DetectProperties.DETECT_PROJECT_VERSION_NAME, TEST_PROJECT_VERSION),
            Pair.of(DetectProperties.DETECT_PROJECT_GROUP_NAME, TEST_PROJECT_GROUP),
            Pair.of(DetectProperties.DETECT_CONTAINER_SCAN_FILE, imageFilePath));
    }

    private OperationRunner setUpDetectConfig(BlackduckScanMode blackduckScanMode, String imageFilePath){
        DetectConfigurationFactory detectConfigurationFactory = makeContainerScanFactory(blackduckScanMode, imageFilePath);
        BootSingletons bootSingletonsMock = Mockito.mock(BootSingletons.class);
        Mockito.when(bootSingletonsMock.getDetectConfigurationFactory()).thenReturn(detectConfigurationFactory);
        return new OperationRunner(
            Mockito.mock(DetectDetectableFactory.class),
            Mockito.mock(DetectFontLoaderFactory.class),
            bootSingletonsMock,
            Mockito.mock(UtilitySingletons.class),
            Mockito.mock(EventSingletons.class)
        );
    }

    private File updateDetectConfigAndGetContainerImage(BlackduckScanMode blackduckScanMode, String imageFilePath) throws IntegrationException, IOException, DetectUserFriendlyException {
        File downloadDirectory = new File("src/test/resources/tool/container.scan");
        OperationRunner operationRunner = setUpDetectConfig(blackduckScanMode, imageFilePath);
        OperationRunner operationRunnerSpy = Mockito.spy(operationRunner);
        Mockito.doReturn(OperationRunnerContainerScanTest.TEST_IMAGE_DOWNLOADED_FILE).when(operationRunnerSpy).downloadContainerImage(gson, downloadDirectory, imageFilePath);
        return operationRunnerSpy.getContainerScanImage(gson, downloadDirectory);
    }

    @Test
    public void testGetContainerScanImageForLocalFilePath() throws DetectUserFriendlyException, IntegrationException, IOException {
        // Intelligent
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.INTELLIGENT, TEST_IMAGE_LOCAL_FILE_PATH);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);

        // Stateless
        containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.STATELESS, TEST_IMAGE_LOCAL_FILE_PATH);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
    }

    @Test
    public void testGetContainerScanImageForImageUrl() throws DetectUserFriendlyException, IntegrationException, IOException {
        // Intelligent
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.INTELLIGENT, TEST_IMAGE_URL);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);

        // Stateless
        containerImageRetrieved = updateDetectConfigAndGetContainerImage(BlackduckScanMode.STATELESS, TEST_IMAGE_URL);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
    }



    @Test
    public void testGetScanServiceDetailsForIntelligent() {
        OperationRunner operationRunner = setUpDetectConfig(BlackduckScanMode.INTELLIGENT, TEST_IMAGE_LOCAL_FILE_PATH);

        // Test if the correct endpoint is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostEndpoint().isEmpty());
        Assertions.assertEquals(INTELLIGENT_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());
        Assertions.assertNotEquals(RAPID_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());

        // Test if the correct content type is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostContentType().isEmpty());
        Assertions.assertEquals(INTELLIGENT_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
        Assertions.assertNotEquals(RAPID_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
    }

    @Test
    public void testGetScanServiceDetailsForStateless() {
        OperationRunner operationRunner = setUpDetectConfig(BlackduckScanMode.STATELESS, TEST_IMAGE_LOCAL_FILE_PATH);

        // Test if the correct endpoint is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostEndpoint().isEmpty());
        Assertions.assertEquals(RAPID_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());
        Assertions.assertNotEquals(INTELLIGENT_SCAN_ENDPOINT, operationRunner.getScanServicePostEndpoint());

        // Test if the correct content type is returned for INTELLIGENT scans
        Assertions.assertFalse(operationRunner.getScanServicePostContentType().isEmpty());
        Assertions.assertEquals(RAPID_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
        Assertions.assertNotEquals(INTELLIGENT_SCAN_CONTENT_TYPE, operationRunner.getScanServicePostContentType());
    }

    @Test void testCreateContainerScanMetadataForIntelligent() {
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.INTELLIGENT;
        NameVersion projectNameVersion = new NameVersion(TEST_PROJECT_NAME, TEST_PROJECT_VERSION);
        OperationRunner operationRunner = setUpDetectConfig(blackduckScanMode, TEST_IMAGE_LOCAL_FILE_PATH);

        JsonObject expectedImageMetadataObject = new JsonObject();
        expectedImageMetadataObject.addProperty("scanId", TEST_SCAN_ID.toString());
        expectedImageMetadataObject.addProperty("scanType", SCAN_TYPE);
        expectedImageMetadataObject.addProperty("scanPersistence", SCAN_PERSISTENCE_TYPE_FOR_INTELLIGENT);
        expectedImageMetadataObject.addProperty("projectName", TEST_PROJECT_NAME);
        expectedImageMetadataObject.addProperty("projectVersionName", TEST_PROJECT_VERSION);
        expectedImageMetadataObject.addProperty("projectGroupName", TEST_PROJECT_GROUP);

        Assertions.assertFalse(expectedImageMetadataObject.isJsonNull());
        Assertions.assertTrue(expectedImageMetadataObject.isJsonObject());
        Assertions.assertEquals(expectedImageMetadataObject, operationRunner.createContainerScanImageMetadata(TEST_SCAN_ID, projectNameVersion));
    }

    @Test void testCreateContainerScanMetadataForStateless() {
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.STATELESS;
        NameVersion projectNameVersion = new NameVersion(TEST_PROJECT_NAME, TEST_PROJECT_VERSION);
        OperationRunner operationRunner = setUpDetectConfig(blackduckScanMode, TEST_IMAGE_LOCAL_FILE_PATH);

        JsonObject expectedImageMetadataObject = new JsonObject();
        expectedImageMetadataObject.addProperty("scanId", TEST_SCAN_ID.toString());
        expectedImageMetadataObject.addProperty("scanType", SCAN_TYPE);
        expectedImageMetadataObject.addProperty("scanPersistence", SCAN_PERSISTENCE_TYPE_FOR_STATELESS);
        expectedImageMetadataObject.addProperty("projectName", TEST_PROJECT_NAME);
        expectedImageMetadataObject.addProperty("projectVersionName", TEST_PROJECT_VERSION);
        expectedImageMetadataObject.addProperty("projectGroupName", TEST_PROJECT_GROUP);

        Assertions.assertFalse(expectedImageMetadataObject.isJsonNull());
        Assertions.assertTrue(expectedImageMetadataObject.isJsonObject());
        Assertions.assertEquals(expectedImageMetadataObject, operationRunner.createContainerScanImageMetadata(TEST_SCAN_ID, projectNameVersion));
    }

}
