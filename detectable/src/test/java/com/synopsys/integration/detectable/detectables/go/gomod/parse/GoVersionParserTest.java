package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.go.gomod.GoVersion;

class GoVersionParserTest {

    @Test
    void parseGoVersion() {
        String goVersionLine = "go version go1.17.5 darwin/amd64";
        GoVersionParser parser = new GoVersionParser();
        Optional<GoVersion> goVersion = parser.parseGoVersion(goVersionLine);

        assertVersion(1, 17, goVersion);
    }

    @Test
    void parseMissingSuffix() {
        GoVersionParser parser = new GoVersionParser();
        String goVersionLine = "go version go1.9";
        Optional<GoVersion> goVersion = parser.parseGoVersion(goVersionLine);

        assertVersion(1, 9, goVersion);
    }

    @Test
    void parseMissingPrefix() {
        GoVersionParser parser = new GoVersionParser();
        String goVersionLine = "go1.2.3  darwin/amd64";
        Optional<GoVersion> goVersion = parser.parseGoVersion(goVersionLine);

        assertVersion(1, 2, goVersion);
    }

    private void assertVersion(int majorVersion, int minorVersion, Optional<GoVersion> goVersion) {
        assertTrue(goVersion.isPresent());
        assertEquals(majorVersion, goVersion.get().getMajorVersion());
        assertEquals(minorVersion, goVersion.get().getMinorVersion());
    }
}