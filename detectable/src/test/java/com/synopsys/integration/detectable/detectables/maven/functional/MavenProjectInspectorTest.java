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
package com.synopsys.integration.detectable.detectables.maven.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class MavenProjectInspectorTest extends DetectableFunctionalTest {

    public MavenProjectInspectorTest() throws IOException {
        super("nuget");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("pom.xml"));

        String source = getSourceDirectory().toFile().getPath();
        File jsonFile = new File(getOutputDirectory().toFile(), "inspection.json");
        String inspector = new File("inspector").getCanonicalPath();
        addExecutableOutput(createStandardOutput(""), inspector, "inspect", "--dir", source, "--output-file", jsonFile.getPath());

        addOutputFile(jsonFile.toPath(), "{\n",
            "   \"Dir\": \"/opt/project/src\",\n",
            "   \"Modules\": {\n",
            "      \"/opt/project/src/pom.xml\": {\n",
            "         \"ModuleFile\": \"/opt/project/src/pom.xml\",\n",
            "         \"ModuleDir\": \"/opt/project/src\",\n",
            "         \"Dependencies\": [\n",
            "            {\n",
            "               \"Id\": \"91390d46-4824-1909-fc05-0d949a4466c8\",\n",
            "               \"IncludedBy\": [\n",
            "                  \"DIRECT\"\n",
            "               ],\n",
            "               \"MavenCoordinates\": {\n",
            "                  \"GroupId\": \"COORDINATE_GROUP\",\n",
            "                  \"ArtifactId\": \"COORDINATE_ARTIFACT\",\n",
            "                  \"Version\": \"COORDINATE_VERSION\"\n",
            "               },\n",
            "               \"DependencyType\": \"MAVEN\",\n",
            "               \"DependencySource\": \"EXTERNAL\",\n",
            "               \"Name\": \"NON_COORDINATE_NAME\",\n",
            "               \"Version\": \"NON_COORDINATE_VERSION\",\n",
            "               \"Artifacts\": [\n",
            "                  \"/root/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar\"\n",
            "               ],\n",
            "               \"Scope\": \"compile\"\n",
            "            }\n",
            "         ],\n",
            "         \"Strategy\": \"MAVEN\"\n",
            "      }\n",
            "   }\n",
            "}\n");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createMavenProjectInspectorDetectable(detectableEnvironment, () -> ExecutableTarget.forFile(new File("inspector")), new MavenParseOptions(false, false), new ProjectInspectorOptions(null));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        List<CodeLocation> codeLocations = extraction.getCodeLocations();

        Assertions.assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);

        Set<Dependency> dependencies = codeLocation.getDependencyGraph().getRootDependencies();
        Assertions.assertEquals(1, dependencies.size());

        Dependency first = dependencies.iterator().next();
        Assertions.assertNotNull(first);

        Assertions.assertEquals("COORDINATE_ARTIFACT", first.getName());
        Assertions.assertEquals("COORDINATE_VERSION", first.getVersion());

        ExternalId firstId = first.getExternalId();
        Assertions.assertEquals("COORDINATE_ARTIFACT", firstId.getName());
        Assertions.assertEquals("COORDINATE_VERSION", firstId.getVersion());
        Assertions.assertEquals("COORDINATE_GROUP", firstId.getGroup());
    }
}
