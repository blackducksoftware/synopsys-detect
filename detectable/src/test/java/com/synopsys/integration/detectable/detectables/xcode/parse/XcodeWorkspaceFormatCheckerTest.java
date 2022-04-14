package com.synopsys.integration.detectable.detectables.xcode.parse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;

class XcodeWorkspaceFormatCheckerTest {
    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void compatibilityTest(String knownVersion) {
        XcodeWorkspaceFormatChecker formatChecker = new XcodeWorkspaceFormatChecker();
        XcodeWorkspace xcodeWorkspace = new XcodeWorkspace(knownVersion, Collections.emptyList());

        assertTrue(formatChecker.checkForVersionCompatibility(xcodeWorkspace));
    }

    @Test
    void incompatibilityTest() {
        XcodeWorkspaceFormatChecker formatChecker = new XcodeWorkspaceFormatChecker();
        String unknownVersion = "some unknown version";
        XcodeWorkspace xcodeWorkspace = new XcodeWorkspace(unknownVersion, Collections.emptyList());
        
        assertFalse(formatChecker.checkForVersionCompatibility(xcodeWorkspace));
    }

    public static Stream<String> knownFileFormats() {
        return Arrays.stream(XcodeWorkspaceFormatChecker.KNOWN_FILE_FORMAT_VERSIONS);
    }
}