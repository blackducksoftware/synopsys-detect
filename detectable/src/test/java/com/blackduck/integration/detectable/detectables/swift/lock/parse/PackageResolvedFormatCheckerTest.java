package com.blackduck.integration.detectable.detectables.swift.lock.parse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import com.blackduck.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PackageResolvedFormatCheckerTest {
    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void compatibilityTest(PackageResolvedFormat knownVersion) {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        assertTrue(formatChecker.checkForVersionCompatibility(knownVersion));
    }

    @Test
    void incompatibilityTest() {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        assertFalse(formatChecker.checkForVersionCompatibility(PackageResolvedFormat.UNKNOWN("something-else")));
    }

    static Stream<PackageResolvedFormat> knownFileFormats() {
        return Arrays.stream(PackageResolvedFormatChecker.getKnownFileFormatVersions());
    }
}