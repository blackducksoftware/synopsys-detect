package com.synopsys.integration.detectable.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FileFormatCheckerTest {
    private static final String[] KNOWN_FILE_FORMATS = { "1", "1.2" };

    private interface TestClass {
        String getVersion();
    }

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void compatibilityTest(String knownVersion) {
        FileFormatChecker formatChecker = new FileFormatChecker(KNOWN_FILE_FORMATS) {
            @Override
            public void handleUnknownVersion(String unknownFileFormat) {
                Assertions.fail(unknownFileFormat + " is a known version and should not have failed the check.");
            }
        };
        TestClass testClass = () -> knownVersion;

        assertTrue(formatChecker.checkForVersionCompatibility(knownVersion));
        assertTrue(formatChecker.checkForVersionCompatibility(testClass::getVersion));
    }

    @Test
    void incompatibilityTest() {
        String unknownVersion = "some unknown version";
        FileFormatChecker formatChecker = new FileFormatChecker("1", "2") {
            @Override
            public void handleUnknownVersion(String unknownFileFormat) {
                Assertions.assertEquals(unknownVersion, unknownFileFormat);
            }
        };
        TestClass testClass = () -> unknownVersion;

        assertFalse(formatChecker.checkForVersionCompatibility(unknownVersion));
        assertFalse(formatChecker.checkForVersionCompatibility(testClass::getVersion));
    }

    public static Stream<String> knownFileFormats() {
        return Arrays.stream(KNOWN_FILE_FORMATS);
    }
}