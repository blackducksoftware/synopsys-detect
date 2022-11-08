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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class XcodeWorkspaceDetectableTest extends DetectableFunctionalTest {
    public XcodeWorkspaceDetectableTest() throws IOException {
        super("XcodeWorkspace");
    }

    @Override
    public void setup() throws IOException {
        // Within the workspace directory
        addFile(
            Paths.get("jake-test.xcworkspace/xcshareddata/swiftpm/Package.resolved"),
            "{",
            "  \"version\": 1,",
            "  \"object\": {",
            "    \"pins\": [",
            "      {",
            "        \"package\": \"swift-argument-parser\",",
            "        \"repositoryURL\": \"https://github.com/apple/swift-argument-parser.git\",",
            "        \"state\": {",
            "          \"branch\": null,",
            "          \"revision\": \"d2930e8fcf9c33162b9fcc1d522bc975e2d4179b\",",
            "          \"version\": \"1.0.1\"",
            "        }",
            "      },",
            "      {",
            "        \"package\": \"BadUrl\",",
            "        \"repositoryURL\": \"invalid/url\",",
            "        \"state\": {",
            "          \"branch\": null,",
            "          \"revision\": \"something123\",",
            "          \"version\": \"1.2.3\"",
            "        }",
            "      }",
            "    ]",
            "  }",
            "}"
        );

        // Defined in workspace data file
        Path projectDirectory = addDirectory(Paths.get("project"));
        addFile(
            projectDirectory.resolve("MyLibrary/Package.resolved"),
            "{",
            "  \"version\": 1,",
            "  \"object\": {",
            "    \"pins\": [",
            "      {",
            "        \"package\": \"Auth0\",",
            "        \"repositoryURL\": \"https://github.com/auth0/Auth0.swift.git\",",
            "        \"state\": {",
            "          \"branch\": null,",
            "          \"revision\": \"8e8a6b0337a27a3342beb72b5407141fdd4a7860\",",
            "          \"version\": \"1.35.0\"",
            "        }",
            "      }",
            "    ]",
            "  }",
            "}"
        );
        addFile(
            projectDirectory.resolve("jakem-test.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved"),
            "{",
            "  \"version\": 1,",
            "  \"object\": {",
            "    \"pins\": [",
            "      {",
            "        \"package\": \"R.swift.Library\",",
            "        \"repositoryURL\": \"https://github.com/mac-cain13/R.swift.Library\",",
            "        \"state\": {",
            "          \"branch\": null,",
            "          \"revision\": \"8998cfe77f4fce79ee6dfab0c88a7d551659d8fb\",",
            "          \"version\": \"5.4.0\"",
            "        }",
            "      }",
            "    ]",
            "  }",
            "}"
        );

        addFile(
            Paths.get("jake-test.xcworkspace/contents.xcworkspacedata"),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<Workspace",
            "   version = \"1.0\">",
            "   <FileRef",
            "      location = \"group:project/MyLibrary\">",
            "   </FileRef>",
            "   <FileRef",
            "      location = \"group:project/jakem-test.xcodeproj\">",
            "   </FileRef>",
            "</Workspace>"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createXcodeWorkspaceDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertEquals(3, extraction.getCodeLocations().size(), "Expected 1 code location from local and 2 defined in the workspace data file.");
        NameVersionGraphAssert graphAssert;

        CodeLocation workspaceLocalCodeLocation = extraction.getCodeLocations().get(0);
        graphAssert = new NameVersionGraphAssert(Forge.GITHUB, workspaceLocalCodeLocation.getDependencyGraph());
        graphAssert.hasRootDependency("apple/swift-argument-parser", "1.0.1");
        graphAssert.hasRootDependency("invalid/url", "1.2.3"); // The GitUrlParser can handle this
        graphAssert.hasRootSize(2);

        CodeLocation myLibraryCodeLocation = extraction.getCodeLocations().get(1);
        graphAssert = new NameVersionGraphAssert(Forge.GITHUB, myLibraryCodeLocation.getDependencyGraph());
        graphAssert.hasRootDependency("auth0/Auth0.swift", "1.35.0");
        graphAssert.hasRootSize(1);

        CodeLocation xcodeProjectCodeLocation = extraction.getCodeLocations().get(2);
        graphAssert = new NameVersionGraphAssert(Forge.GITHUB, xcodeProjectCodeLocation.getDependencyGraph());
        graphAssert.hasRootDependency("mac-cain13/R.swift.Library", "5.4.0");
        graphAssert.hasRootSize(1);
    }

}