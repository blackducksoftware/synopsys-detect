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
package com.synopsys.integration.detectable.detectables.cocoapods.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PodlockTransitiveDetectableTest extends DetectableFunctionalTest {
    public PodlockTransitiveDetectableTest() throws IOException {
        super("podlock-transitive");
    }

    @Override
    public void setup() throws IOException {

        addFile(Paths.get("Podfile.lock"),
            "PODS:",
            "  - OktaDeviceSDK (0.0.1):",
            "    - JOSESwift (= 1.8.1)",
            "",
            "DEPENDENCIES:",
            "  - OktaDeviceSDK (from `https://github.com/okta/okta-devices-swift.git`, commit `89ee1a16ff37a17604c48fa277b7e86a0756bddf`)"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment environment) {
        return detectableFactory.createPodLockDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.COCOAPODS, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("OktaDeviceSDK", "0.0.1");
    }
}