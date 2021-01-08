package com.synopsys.integration.detect.lifecycle.boot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.OperatingSystemType;

public class DetectBootTest {
    public static final String EXPECTED_RUN_ID = "TEST_ID";
    public static final String EXPECTED_VERSION_TEXT = "TEST_FULL_VERSION";
    public static final int EXPECTED_MAJOR_VERSION = 6;
    public static final OperatingSystemType CURRENT_OS = OperatingSystemType.determineFromSystem();

    public DetectBoot detectBoot;
    public DetectRun detectRun;
    public DetectContext detectContext;
    public EventSystem eventSystem;

    @BeforeEach
    public void setUp() {
        DetectBootFactory detectBootFactory = new DetectBootFactory();
        detectBoot = new DetectBoot(detectBootFactory, , , );
        detectRun = new DetectRun(EXPECTED_RUN_ID);
        eventSystem = new EventSystem();
        DetectInfo detectInfo = new DetectInfo(EXPECTED_VERSION_TEXT, EXPECTED_MAJOR_VERSION, CURRENT_OS);
        Gson gson = new Gson();

        detectContext = new DetectContext(detectRun);
        detectContext.registerBean(detectInfo);
        detectContext.registerBean(gson);
    }

    @Test
    public void testOffline() {
        MockEnvironment configurableEnvironment = new MockEnvironment();
        configurableEnvironment.setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty().getKey(), "true");
        String[] sourceArgs = new String[]{};

        try {
            Optional<DetectBootResult> wrappedDetectBootResult = detectBoot.boot(detectRun, sourceArgs, configurableEnvironment, eventSystem, detectContext);
            assertTrue(wrappedDetectBootResult.isPresent());
            DetectBootResult detectBootResult = wrappedDetectBootResult.get();
            assertEquals(DetectBootResult.BootType.RUN, detectBootResult.getBootType());

            Optional<ProductRunData> wrappedProductRunData = detectBootResult.getProductRunData();
            assertTrue(wrappedProductRunData.isPresent());
            ProductRunData productRunData = wrappedProductRunData.get();
            assertTrue(productRunData.shouldUseBlackDuckProduct());
            assertFalse(productRunData.shouldUsePolarisProduct());
            assertFalse(productRunData.getBlackDuckRunData().isOnline());
        } catch (DetectUserFriendlyException | IOException | IllegalAccessException e) {
            fail("Unexpected exception was thrown by the test code: ", e);
        }
    }

    @ValueSource(strings = { "-h", "-hjson", "--help", "--helpjson"} )
    @ParameterizedTest
    public void testHelp(String sourceArg) {
        MockEnvironment configurableEnvironment = new MockEnvironment();
        configurableEnvironment.setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty().getKey(), "true");
        String[] sourceArgs = new String[]{sourceArg};

        try {
            Optional<DetectBootResult> wrappedDetectBootResult = detectBoot.boot(detectRun, sourceArgs, configurableEnvironment, eventSystem, detectContext);
            assertTrue(wrappedDetectBootResult.isPresent());
            DetectBootResult detectBootResult = wrappedDetectBootResult.get();
            assertEquals(DetectBootResult.BootType.EXIT, detectBootResult.getBootType());
        } catch (DetectUserFriendlyException | IOException | IllegalAccessException e) {
            fail("Unexpected exception was thrown by the test code: ", e);
        }
    }
}
