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
package com.synopsys.integration.detectable.detectables.yarn.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class YarnLockDetectableTest extends DetectableFunctionalTest {

    public YarnLockDetectableTest() throws IOException {
        super("yarn");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("yarn.lock"),
            "async@2.5.0:",
            "   version \"2.5.0\"",
            "   dependencies:",
            "     lodash \"4.17.4\"",
            "",
            "lodash@npm:4.17.4",
            "   version \"4.17.4\""
        );

        addFile(Paths.get("package.json"),
            "{",
            "   \"name\": \"babel\",",
            "   \"version\": \"1.2.3\",",
            "   \"private\": true,",
            "   \"license\": \"MIT\",",
            "   \"dependencies\": { ",
            "       \"async\": \"2.5.0\",",
            "       \"lodash\": \"4.17.4\"",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createYarnLockDetectable(detectableEnvironment, new YarnLockOptions(true));
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());
        final CodeLocation codeLocation = extraction.getCodeLocations().get(0);

        Assertions.assertEquals("babel", extraction.getProjectName());
        Assertions.assertEquals("1.2.3", extraction.getProjectVersion());

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, codeLocation.getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("async", "2.5.0");
        graphAssert.hasRootDependency("lodash", "4.17.4");
        graphAssert.hasParentChildRelationship("async", "2.5.0", "lodash", "4.17.4");
    }
}
