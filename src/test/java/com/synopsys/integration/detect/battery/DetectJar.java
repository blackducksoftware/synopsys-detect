package com.synopsys.integration.detect.battery;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class DetectJar {
    private final String java;
    private final String jar;

    public DetectJar(final String java, final String jar) {
        this.java = java;
        this.jar = jar;
    }

    public static Optional<DetectJar> locateJar() {
        String java = System.getenv("BATTERY_TESTS_JAVA_PATH");
        String detectJar = System.getenv("BATTERY_TESTS_DETECT_JAR_PATH");
        boolean bothExist = StringUtils.isNotBlank(java) && StringUtils.isNotBlank(detectJar) && new File(java).exists() && new File(detectJar).exists();
        if (bothExist) {
            return Optional.of(new DetectJar(java, detectJar));
        } else {
            return Optional.empty();
        }
    }

    public String getJar() {
        return jar;
    }

    public String getJava() {
        return java;
    }
}
