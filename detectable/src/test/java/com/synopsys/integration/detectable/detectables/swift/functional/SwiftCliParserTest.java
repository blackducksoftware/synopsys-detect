package com.synopsys.integration.detectable.detectables.swift.functional;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliParser;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

class SwiftCliParserTest {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final SwiftCliParser swiftCliParser = new SwiftCliParser(gson);

    @Test
    void parseCleanOutput() {
        final List<String> lines = FunctionalTestFiles.asListOfStrings("/swift/cleanOutput.txt");
        final SwiftPackage swiftPackage = swiftCliParser.parseOutput(lines);
        testResults(swiftPackage);
    }

    @Test
    void parseNoisyOutput() {
        final List<String> lines = FunctionalTestFiles.asListOfStrings("/swift/noisyOutput.txt");
        final SwiftPackage swiftPackage = swiftCliParser.parseOutput(lines);
        testResults(swiftPackage);
    }

    private void testResults(final SwiftPackage swiftPackage) {
        Assertions.assertEquals("DeckOfPlayingCards", swiftPackage.getName());
        Assertions.assertEquals("unspecified", swiftPackage.getVersion());

        Assertions.assertEquals(2, swiftPackage.getDependencies().size());
        for (final SwiftPackage dependency : swiftPackage.getDependencies()) {
            if (dependency.getName().equals("FisherYates")) {
                Assertions.assertEquals("2.0.5", dependency.getVersion());
                Assertions.assertEquals(0, dependency.getDependencies().size());
            } else if (dependency.getName().equals("PlayingCard")) {
                Assertions.assertEquals("3.0.5", dependency.getVersion());
                Assertions.assertEquals(0, dependency.getDependencies().size());
            } else {
                Assertions.fail(String.format("Found unexpected dependency: %s==%s", dependency.getName(), dependency.getVersion()));
            }
        }
    }
}