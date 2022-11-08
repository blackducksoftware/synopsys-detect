/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.xcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class XcodeProjectDetectableTest extends DetectableFunctionalTest {
    public XcodeProjectDetectableTest() throws IOException {
        super("XcodeProject");
    }

    @Override
    public void setup() throws IOException {
        addFileFromResources(Paths.get("jakem-test.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved"), "/xcode/Package.resolved");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createXcodeProjectDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertEquals(1, extraction.getCodeLocations().size(), "Expected only one code location.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootDependency("apple/swift-argument-parser", "1.0.1");
        graphAssert.hasRootDependency("auth0/Auth0.swift", "1.35.0");
        graphAssert.hasRootDependency("mac-cain13/R.swift.Library", "5.4.0");

        // The GitUrlParser can handle this
        graphAssert.hasRootDependency("invalid/url", "1.2.3");

        graphAssert.hasRootSize(4);
    }
}