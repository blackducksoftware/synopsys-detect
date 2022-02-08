package com.synopsys.integration.detectable.detectables.xcode.process;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;

class PackageResolvedFormatCheckerTest {

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void compatibilityTest(String knownVersion) {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        PackageResolved packageResolved = new PackageResolved(null, knownVersion);
        formatChecker.handleVersionCompatibility(packageResolved, (version, knownVersions) -> Assertions.fail(version + " is a known version and should not have failed the check."));
    }

    @Test
    void incompatibilityTest() {
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        String unknownVersion = "some unknown version";
        PackageResolved packageResolved = new PackageResolved(null, unknownVersion);
        formatChecker.handleVersionCompatibility(packageResolved, (version, knownVersions) -> Assertions.assertEquals(unknownVersion, version));
    }

    public static Stream<String> knownFileFormats() {
        return Arrays.stream(PackageResolvedFormatChecker.KNOWN_FILE_FORMAT_VERSIONS);
    }
}