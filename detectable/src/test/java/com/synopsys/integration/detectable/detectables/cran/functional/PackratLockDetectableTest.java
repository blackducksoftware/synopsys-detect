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
package com.synopsys.integration.detectable.detectables.cran.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PackratLockDetectableTest extends DetectableFunctionalTest {
    public PackratLockDetectableTest() throws IOException {
        super("packrat");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("packrat.lock"),
            "This is a bogus line",
            "PackratFormat: 1.4",
            "PackratVersion: 0.4.9.3",
            "RVersion: 3.5.1",
            "Repos: BioCsoft=https://bioconductor.org/packages/3.8/bioc.",
            "   CRAN=https://cran.uni-muenster.de/",
            "",
            "Package: backports",
            "Source: CRAN",
            "Version: 1.1.2",
            "Hash: 5ae7b3466e529e4400951ca18c137e40",
            "",
            "Package: checkmate",
            "Source: CRAN",
            "Version: 1.8.5",
            "Hash: e1bbc5228ab3da931a099208bc95ad23",
            "Requires: backports",
            "",
            "Package: BBmisc",
            "Source: CRAN",
            "Version: 1.11",
            "Hash: c9b8888a595ca8153ee1ed47bd8f771c",
            "Requires: checkmate"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createPackratLockDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRAN, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("BBmisc", "1.11");
        graphAssert.hasRootDependency("checkmate", "1.8.5");
        graphAssert.hasRootDependency("backports", "1.1.2");
        graphAssert.hasParentChildRelationship("BBmisc", "1.11", "checkmate", "1.8.5");
        graphAssert.hasParentChildRelationship("checkmate", "1.8.5", "backports", "1.1.2");
    }
}
