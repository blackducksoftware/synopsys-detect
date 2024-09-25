package com.blackduck.integration.detectable.detectables.gradle.inspection.parse;

import com.blackduck.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import com.blackduck.integration.util.NameVersion;

class GradleRootMetadataParserTest {

    @Test
    void parseRootProjectNameVersion() {
        List<String> lines = FunctionalTestFiles.asListOfStrings("/gradle/rootProjectMetadata.txt");
        GradleRootMetadataParser parser = new GradleRootMetadataParser();

        NameVersion nameVersion = parser.parseRootProjectNameVersion(lines);
        assertEquals("detect", nameVersion.getName());
        assertEquals("10.0.0", nameVersion.getVersion());
    }
}
