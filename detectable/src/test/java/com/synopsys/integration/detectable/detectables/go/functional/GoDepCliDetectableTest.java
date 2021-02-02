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
package com.synopsys.integration.detectable.detectables.go.functional;

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

public class GoDepCliDetectableTest extends DetectableFunctionalTest {
    public GoDepCliDetectableTest() throws IOException {
        super("godep");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("Gopkg.toml"));

        addFile(Paths.get("Gopkg.lock"),
            "[[projects]]",
            "   name = \"github.com/davecgh/go-spew\"",
            "   packages = [\"spew\"]",
            "   version = \"v1.1.0\"",
            "",
            "[[projects]]",
            "   branch = \"master\"",
            "   name = \"github.com/golang/protobuf\"",
            "   packages = [\"proto\"]",
            "",
            "[[projects]]",
            "   name = \"github.com/gorilla/context\"",
            "   packages = [\".\"]",
            "   version = \"v1.1\""
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGoLockDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("github.com/davecgh/go-spew/spew", "v1.1.0");
        graphAssert.hasRootDependency("github.com/golang/protobuf/proto", null);
        graphAssert.hasRootDependency("github.com/gorilla/context", "v1.1");
    }

}
