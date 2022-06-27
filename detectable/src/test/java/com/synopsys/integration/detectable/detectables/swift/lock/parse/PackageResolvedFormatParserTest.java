package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;

class PackageResolvedFormatParserTest {

    @ParameterizedTest
    @MethodSource("knownVersions")
    void parseFormatFromJson(PackageResolvedFormat knownVersion) {
        String json = StringUtils.joinWith(
            System.lineSeparator(),
            "{",
            "\"version\": \"" + knownVersion.getVersionString() + "\"",
            "}"
        );

        PackageResolvedFormatParser parser = new PackageResolvedFormatParser(new Gson());
        PackageResolvedFormat packageResolvedFormat = parser.parseFormatFromJson(json);
        assertEquals(knownVersion, packageResolvedFormat);
    }

    @ParameterizedTest
    @MethodSource("knownVersions")
    void parseFormatFromVersion(PackageResolvedFormat knownVersion) {
        PackageResolvedFormatParser parser = new PackageResolvedFormatParser(new Gson());
        PackageResolvedFormat packageResolvedFormat = parser.parseFormatFromVersion(knownVersion.getVersionString());
        assertEquals(knownVersion, packageResolvedFormat);
    }

    @Test
    void parseJsonForUnknown() {
        String json = StringUtils.joinWith(
            System.lineSeparator(),
            "{",
            "\"version\": 62",
            "}"
        );

        PackageResolvedFormatParser parser = new PackageResolvedFormatParser(new Gson());
        PackageResolvedFormat packageResolvedFormat = parser.parseFormatFromJson(json);
        assertEquals(PackageResolvedFormat.UNKNOWN("62"), packageResolvedFormat);
    }

    @Test
    void parseFormatForUnknown() {
        PackageResolvedFormatParser parser = new PackageResolvedFormatParser(new Gson());
        PackageResolvedFormat packageResolvedFormat = parser.parseFormatFromVersion("62");
        assertEquals(PackageResolvedFormat.UNKNOWN("62"), packageResolvedFormat);
    }

    static Stream<PackageResolvedFormat> knownVersions() {
        return Arrays.stream(PackageResolvedFormatChecker.getKnownFileFormatVersions());
    }
}