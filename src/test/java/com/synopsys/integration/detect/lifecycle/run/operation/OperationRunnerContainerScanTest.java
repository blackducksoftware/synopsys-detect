package com.synopsys.integration.detect.lifecycle.run.operation;

import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.exception.IntegrationException;

public class OperationRunnerContainerScanTest {
    private static final Gson gson = new Gson();
    private static final String TEST_IMAGE_LOCAL_FILE_PATH = "src/test/resources/tool/container.scan/testImage.tar";
    private static final String TEST_IMAGE_URL = "https://www.container.artifactory.com/testImage.tar";
    private static final File TEST_IMAGE_LOCAL_FILE = new File("src/test/resources/tool/container.scan/testImage.tar");
    private static final File TEST_IMAGE_DOWNLOADED_FILE = new File("src/test/resources/tool/container.scan/testImageDownloaded.tar");

    private File updateDetectConfigAndGetContainerImage(String imageFilePath) throws IntegrationException, IOException, DetectUserFriendlyException {
        File downloadDirectory = new File("src/test/resources/tool/container.scan");

        // Creates a custom mocked instance of Detect's configuration factory for given image file path
        DetectConfigurationFactory detectConfigurationFactory = factoryOf(
            Pair.of(DetectProperties.DETECT_TOOLS, DetectTool.CONTAINER_SCAN.toString()),
            Pair.of(DetectProperties.DETECT_CONTAINER_SCAN_FILE, imageFilePath));

        BootSingletons bootSingletonsMock = Mockito.mock(BootSingletons.class);
        Mockito.when(bootSingletonsMock.getDetectConfigurationFactory()).thenReturn(detectConfigurationFactory);

        OperationRunner operationRunner = new OperationRunner(
            Mockito.mock(DetectDetectableFactory.class),
            Mockito.mock(DetectFontLoaderFactory.class),
            bootSingletonsMock,
            Mockito.mock(UtilitySingletons.class),
            Mockito.mock(EventSingletons.class)
        );

        OperationRunner operationRunnerSpy = Mockito.spy(operationRunner);
        Mockito.doReturn(OperationRunnerContainerScanTest.TEST_IMAGE_DOWNLOADED_FILE).when(operationRunnerSpy).downloadContainerImage(gson, downloadDirectory, imageFilePath);
        return operationRunnerSpy.getContainerScanImage(gson, downloadDirectory);
    }

    @Test
    public void testGetContainerScanImageForLocalFilePath() throws DetectUserFriendlyException, IntegrationException, IOException {
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(TEST_IMAGE_LOCAL_FILE_PATH);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
    }

    @Test
    public void testGetContainerScanImageForImageUrl() throws DetectUserFriendlyException, IntegrationException, IOException {
        File containerImageRetrieved = updateDetectConfigAndGetContainerImage(TEST_IMAGE_URL);
        Assertions.assertTrue(containerImageRetrieved != null && containerImageRetrieved.exists());
        Assertions.assertEquals(TEST_IMAGE_DOWNLOADED_FILE, containerImageRetrieved);
        Assertions.assertNotEquals(TEST_IMAGE_LOCAL_FILE, containerImageRetrieved);
    }
}
