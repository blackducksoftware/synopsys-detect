package com.blackducksoftware.integration.hub.detect;

import java.util.Arrays;
import java.util.List;

public enum DetectTool {
    DETECTOR,
    SIGNATURE_SCAN,
    BINARY_SCAN,
    SWIP_CLI,
    DOCKER;

    public static List<DetectTool> DEFAULT_PROJECT_ORDER = Arrays.asList(DOCKER, DETECTOR);

}
