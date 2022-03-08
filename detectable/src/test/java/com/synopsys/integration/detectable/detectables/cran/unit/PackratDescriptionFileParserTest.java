package com.synopsys.integration.detectable.detectables.cran.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cran.parse.PackratDescriptionFileParser;
import com.synopsys.integration.util.NameVersion;

class PackratDescriptionFileParserTest {
    private PackratDescriptionFileParser packratDescriptionFileParser;
    private List<String> validDescriptionFileLines;
    private List<String> invalidDescriptionFileLines;

    @BeforeEach
    void setUp() {
        packratDescriptionFileParser = new PackratDescriptionFileParser();

        validDescriptionFileLines = new ArrayList<>();
        validDescriptionFileLines.add("Package:TestProjectName ");
        validDescriptionFileLines.add("Type: Package");
        validDescriptionFileLines.add("Title: A test title");
        validDescriptionFileLines.add("    Dependencies");
        validDescriptionFileLines.add("Version:1.0.0 ");
        validDescriptionFileLines.add("Author: Test");

        invalidDescriptionFileLines = new ArrayList<>();
        invalidDescriptionFileLines.add("Package invalidProjectName");
        invalidDescriptionFileLines.add("Type: Package");
        invalidDescriptionFileLines.add("Title: A test title");
        invalidDescriptionFileLines.add("    Dependencies");
        invalidDescriptionFileLines.add("Version bad");
        invalidDescriptionFileLines.add("Author: Test");
    }

    @Test
    void getProjectNameVersion() {
        NameVersion nameVersion = packratDescriptionFileParser.getProjectNameVersion(validDescriptionFileLines, "", "");
        Assertions.assertEquals("TestProjectName", nameVersion.getName());
        Assertions.assertEquals("1.0.0", nameVersion.getVersion());

        nameVersion = packratDescriptionFileParser.getProjectNameVersion(invalidDescriptionFileLines, "nameDefault", "versionDefault");
        Assertions.assertEquals("nameDefault", nameVersion.getName());
        Assertions.assertEquals("versionDefault", nameVersion.getVersion());
    }
}