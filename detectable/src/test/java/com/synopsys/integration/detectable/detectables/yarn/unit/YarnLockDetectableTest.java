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
package com.synopsys.integration.detectable.detectables.yarn.unit;

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

public class YarnLockDetectableTest extends DetectableFunctionalTest {

    public YarnLockDetectableTest() throws IOException {
        super("yarn");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("yarn.lock"),
            "\"@cogizmo/cogizmo@^0.5.5\":",
            "   version \"0.5.5\"",
            "   resolved \"https://registry.yarnpkg.com/cogizmo-0.5.5\"",
            "   integrity sha512-a9gxpmdXtZEInkCSHUJDLHZVBgb1QS0jhss4cPP93EW7s+uC5bikET2twEF3KV+7rDblJcmNvTR7VJejqd2C2g==",
            "   dependencies:",
            "       \"name1\" \"version1\"",
            "       \"name2\" \"version2\"",
            "",
            "\"@name1\":",
            "   version \"version1\"",
            "   resolved \"https://registry.yarnpkg.com/name1-version1\"",
            "   integrity sha512-a9gxpmdXtZEInkCSHUJDLHZVBgb1QS0jhss4cPP93EW7s+uC5bikET2twEF3KV+7rDblJcmNvTR7VJejqd2C2g==",
            "   dependencies:",
            "",
            "\"@name2\":",
            "   version \"version2\"",
            "   resolved \"https://registry.yarnpkg.com/name2-version2\"",
            "   integrity sha512-a9gxpmdXtZEInkCSHUJDLHZVBgb1QS0jhss4cPP93EW7s+uC5bikET2twEF3KV+7rDblJcmNvTR7VJejqd2C2g==",
            "   dependencies:"
        );

        addFile(Paths.get("package.json"),
            "{",
            "   \"name\": \"@cogizmo/cogizmo\",",
            "   \"version\": \"0.5.5\",",
            "   \"dependencies\": {",
            "   \"name1\": \"version1\",",
            "   \"name2\": \"version2\"",
            "   }",
            "}"
            /*
            "   },",
            "   {",
            "       \"name\": \"@name1\",",
            "       \"version\": \"version1\",",
            "       \"dependencies\": {}",
            "   },",
            "   {",
            "       \"name\": \"@name2\",",
            "       \"version\": \"version2\",",
            "       \"dependencies\": {}",
            "   }",
            "}"

             */
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createYarnLockDetectable(detectableEnvironment, false);
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());
    }
}
