package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.util.NameVersion;

class GradleRootMetadataParserTest {

    @Test
    void parseRootProjectNameVersion() {
        List<String> lines = FunctionalTestFiles.asListOfStrings("/gradle/rootProjectMetadata.txt");
        GradleRootMetadataParser parser = new GradleRootMetadataParser();

        NameVersion nameVersion = parser.parseRootProjectNameVersion(lines);
        assertEquals("synopsys-detect", nameVersion.getName());
        assertEquals("7.5.0", nameVersion.getVersion());
    }
}
