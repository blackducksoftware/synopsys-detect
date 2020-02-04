/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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