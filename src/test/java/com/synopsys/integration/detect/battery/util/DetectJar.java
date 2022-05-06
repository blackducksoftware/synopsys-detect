package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

public class DetectJar {
    private final String java;
    private final String jar;

    public DetectJar(String java, String jar) {
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

    public static File findJar() {
        File libs = TestPaths.libs();
        Assertions.assertNotNull(libs, "Could not locate the libs directory, does libs exist, was the project built?");
        Assertions.assertTrue(libs.exists(), "Could not locate the libs directory, does libs exist, was the project built?");

        File[] libChildren = libs.listFiles();

        Assertions.assertNotNull(libChildren, "Could not locate at least one child of libs, does libs exist, was the project built?");
        // Be sure to clean the project if testing multiple SNAPSHOT versions locally
        Assertions.assertEquals(
            1, libChildren.length,
            "Found an unexpected number of jars, only expecting ONE, aka THE JAR expected to run."
                + System.lineSeparator() + "Found files: " + System.lineSeparator()
                + StringUtils.joinWith(System.lineSeparator(), (Object[]) libChildren)
        );
        File detectJarFile = libChildren[0];

        Assertions.assertNotNull(detectJarFile, "Could not find a detect jar!");
        Assertions.assertTrue(detectJarFile.getName().endsWith(".jar"), "Unknown file type, must find a detect jar to run!");

        return detectJarFile;
    }

    public String getJar() {
        return jar;
    }

    public String getJava() {
        return java;
    }
}
