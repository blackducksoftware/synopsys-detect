package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolved;

class PackageResolvedFormatCheckerTest {
    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void compatibilityTest(String knownVersion) {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        PackageResolved packageResolved = new PackageResolved(null, knownVersion);

        assertTrue(formatChecker.checkForVersionCompatibility(packageResolved));
    }

    @Test
    void incompatibilityTest() {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        String unknownVersion = "some unknown version";
        PackageResolved packageResolved = new PackageResolved(null, unknownVersion);
        
        assertFalse(formatChecker.checkForVersionCompatibility(packageResolved));
    }

    public static Stream<String> knownFileFormats() {
        return Arrays.stream(PackageResolvedFormatChecker.KNOWN_FILE_FORMAT_VERSIONS);
    }
}