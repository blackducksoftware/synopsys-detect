package com.blackduck.integration.detectable.util;

import java.io.File;

import com.blackduck.integration.detectable.DetectableEnvironment;

public class MockDetectableEnvironment extends DetectableEnvironment {

    public MockDetectableEnvironment(File directory) {
        super(directory);
    }

    public static MockDetectableEnvironment empty() {
        return new MockDetectableEnvironment(new File("."));
    }
}
