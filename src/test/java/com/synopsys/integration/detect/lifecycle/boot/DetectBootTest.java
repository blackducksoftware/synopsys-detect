package com.synopsys.integration.detect.lifecycle.boot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.finder.WildcardFileFinder;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.OperatingSystemType;

public class DetectBootTest {
    public static final String TEST_PROPERTY_SOURCE_NAME = "TEST_PROPERTY_SOURCE";
    public static final String EXPECTED_RUN_ID = "TEST_ID";
    public static final String EXPECTED_VERSION_TEXT = "TEST_FULL_VERSION";
    public static final int EXPECTED_MAJOR_VERSION = 6;
    public static final OperatingSystemType CURRENT_OS = OperatingSystemType.determineFromSystem();

    public DetectContext detectContext;
    public DetectBootFactory detectBootFactory;

    @BeforeEach
    public void setUp() {
        DetectRun detectRun = new DetectRun(EXPECTED_RUN_ID);
        DetectInfo detectInfo = new DetectInfo(EXPECTED_VERSION_TEXT, EXPECTED_MAJOR_VERSION, CURRENT_OS);
        Gson gson = new Gson();
        EventSystem eventSystem = new EventSystem();
        FileFinder fileFinder = new WildcardFileFinder();
        detectBootFactory = new DetectBootFactory(detectRun, detectInfo, gson, eventSystem, fileFinder);

        detectContext = new DetectContext(detectRun);
        detectContext.registerBean(detectInfo);
        detectContext.registerBean(gson);

    }

    @Test
    public void testOffline() {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty().getKey(), "true");
        List<PropertySource> propertySources = Collections.singletonList(new MapPropertySource(TEST_PROPERTY_SOURCE_NAME, propertyMap));
        String[] sourceArgs = new String[]{};

        try {
            DetectBoot detectBoot = detectBootFactory.createDetectBoot(propertySources, sourceArgs, detectContext);
            Optional<DetectBootResult> wrappedDetectBootResult = detectBoot.boot(EXPECTED_VERSION_TEXT);
            assertTrue(wrappedDetectBootResult.isPresent());
            DetectBootResult detectBootResult = wrappedDetectBootResult.get();
            assertEquals(DetectBootResult.BootType.RUN, detectBootResult.getBootType());

            Optional<ProductRunData> wrappedProductRunData = detectBootResult.getProductRunData();
            assertTrue(wrappedProductRunData.isPresent());
            ProductRunData productRunData = wrappedProductRunData.get();
            assertTrue(productRunData.shouldUseBlackDuckProduct());
            assertFalse(productRunData.shouldUsePolarisProduct());
            assertFalse(productRunData.getBlackDuckRunData().isOnline());
        } catch (IOException | IllegalAccessException e) {
            fail("Unexpected exception was thrown by the test code: ", e);
        }
    }

    @ValueSource(strings = { "-h", "--help"} )
    @ParameterizedTest
    public void testHelp(String sourceArg) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty().getKey(), "true");
        List<PropertySource> propertySources = Collections.singletonList(new MapPropertySource(TEST_PROPERTY_SOURCE_NAME, propertyMap));
        String[] sourceArgs = new String[]{sourceArg};

        try {
            DetectBoot detectBoot = detectBootFactory.createDetectBoot(propertySources, sourceArgs, detectContext);
            Optional<DetectBootResult> wrappedDetectBootResult = detectBoot.boot(EXPECTED_VERSION_TEXT);
            assertTrue(wrappedDetectBootResult.isPresent());
            DetectBootResult detectBootResult = wrappedDetectBootResult.get();
            assertEquals(DetectBootResult.BootType.EXIT, detectBootResult.getBootType());
        } catch (IOException | IllegalAccessException e) {
            fail("Unexpected exception was thrown by the test code: ", e);
        }
    }
}
