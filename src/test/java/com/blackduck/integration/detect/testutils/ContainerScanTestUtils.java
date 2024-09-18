package com.blackduck.integration.detect.testutils;

import static com.blackduck.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;

import java.io.File;
import java.util.UUID;

import com.blackduck.integration.detect.configuration.DetectConfigurationFactory;
import com.blackduck.integration.detect.configuration.DetectProperties;
import com.blackduck.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import com.blackduck.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.blackduck.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.blackduck.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.blackduck.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.blackduck.integration.detect.workflow.event.EventSystem;
import com.blackduck.integration.detect.workflow.status.OperationSystem;
import com.blackduck.integration.detect.workflow.status.StatusEventPublisher;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import com.blackduck.integration.detect.lifecycle.run.step.utility.OperationWrapper;

public class ContainerScanTestUtils {
    public static final String SCAN_TYPE = "CONTAINER";
    public static final String SCAN_PERSISTENCE_TYPE_FOR_STATELESS = "STATELESS";
    public static final String SCAN_PERSISTENCE_TYPE_FOR_INTELLIGENT = "STATEFUL";
    public static final File TEST_DOWNLOAD_DIRECTORY = new File("src/test/resources/tool/container.scan");
    public static final String TEST_IMAGE_LOCAL_FILE_PATH = "src/test/resources/tool/container.scan/testImage.tar";
    public static final File TEST_IMAGE_LOCAL_FILE = new File(TEST_IMAGE_LOCAL_FILE_PATH);
    public static final String TEST_IMAGE_URL = "https://www.container.artifactory.com/testImage.tar";
    public static final File TEST_IMAGE_DOWNLOADED_FILE = new File("src/test/resources/tool/container.scan/testImageDownloaded.tar");
    public static final String RAPID_SCAN_ENDPOINT = "/api/developer-scans";
    public static final String RAPID_SCAN_CONTENT_TYPE = "application/vnd.blackducksoftware.scan-evidence-1+protobuf";
    public static final String INTELLIGENT_SCAN_ENDPOINT = "/api/intelligent-persistence-scans";
    public static final String INTELLIGENT_SCAN_CONTENT_TYPE = "application/vnd.blackducksoftware.intelligent-persistence-scan-3+protobuf";
    public static final String TEST_PROJECT_GROUP = "DEFAULT_PROJECT_GROUP";
    public static final String TEST_PROJECT_NAME = "DEFAULT_PROJECT_NAME";
    public static final String TEST_PROJECT_VERSION = "DEFAULT_PROJECT_VERSION";
    public static final UUID TEST_SCAN_ID = UUID.randomUUID();

    public DetectConfigurationFactory makeContainerScanFactory(BlackduckScanMode blackduckScanMode, String imageFilePath) {
        return factoryOf(
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, blackduckScanMode.toString()),
            Pair.of(DetectProperties.DETECT_TOOLS, DetectTool.CONTAINER_SCAN.toString()),
            Pair.of(DetectProperties.DETECT_PROJECT_NAME, ContainerScanTestUtils.TEST_PROJECT_NAME),
            Pair.of(DetectProperties.DETECT_PROJECT_VERSION_NAME, ContainerScanTestUtils.TEST_PROJECT_VERSION),
            Pair.of(DetectProperties.DETECT_PROJECT_GROUP_NAME, ContainerScanTestUtils.TEST_PROJECT_GROUP),
            Pair.of(DetectProperties.DETECT_CONTAINER_SCAN_FILE, imageFilePath));
    }

    public OperationRunner setUpDetectConfig(BlackduckScanMode blackduckScanMode, String imageFilePath){
        DetectConfigurationFactory detectConfigurationFactory = makeContainerScanFactory(blackduckScanMode, imageFilePath);
        BootSingletons bootSingletonsMock = Mockito.mock(BootSingletons.class);
        Mockito.when(bootSingletonsMock.getDetectConfigurationFactory()).thenReturn(detectConfigurationFactory);

        // Mock utility singletons
        UtilitySingletons utilitySingletonsMock = Mockito.mock(UtilitySingletons.class);

        // Create mocked operation wrapper
        OperationWrapper operationWrapper = new OperationWrapper();
        Mockito.when(utilitySingletonsMock.getOperationWrapper()).thenReturn(operationWrapper);

        // Create mocked operation system
        EventSystem eventSystem = new EventSystem();
        StatusEventPublisher statusEventPublisher = new StatusEventPublisher(eventSystem);
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        Mockito.when(utilitySingletonsMock.getOperationSystem()).thenReturn(operationSystem);

        return new OperationRunner(
            Mockito.mock(DetectDetectableFactory.class),
            Mockito.mock(DetectFontLoaderFactory.class),
            bootSingletonsMock,
            utilitySingletonsMock,
            Mockito.mock(EventSingletons.class)
        );
    }
}
