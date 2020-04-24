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
package com.synopsys.integration.detectable.detectables.gradle.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class GradleParseDetectableTest extends DetectableFunctionalTest {

    public GradleParseDetectableTest() throws IOException {
        super("gradle-parse");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("build.gradle"),
            "buildscript {",
            "    repositories {",
            "        jcenter()",
            "        mavenCentral()",
            "        maven { url 'https://plugins.gradle.org/m2/' }",
            "    }",
            "    dependencies { classpath 'com.blackducksoftware.integration:common-gradle-plugin:0.0.+' }",
            "}",
            "",
            "version = '13.1.1-SNAPSHOT'",
            "",
            "apply plugin: 'com.blackducksoftware.integration.library'",
            "",
            "dependencies {",
            "    compile 'org.apache.httpcomponents:httpmime:4.5.6'",
            "    compile group: 'commons-collections', name: 'commons-collections', version: '3.2.2'",
            "    compile 'org.apache.commons:commons-lang3:3.7'",
            "}"
        );
    }

    @Override
    @NotNull
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGradleParseDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.MAVEN, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(4);

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId commonsLangExternalId = externalIdFactory.createMavenExternalId("org.apache.commons", "commons-lang3", "3.7");
        graphAssert.hasRootDependency(commonsLangExternalId);

        ExternalId commonsCollectionsExternalId = externalIdFactory.createMavenExternalId("commons-collections", "commons-collections", "3.2.2");
        graphAssert.hasRootDependency(commonsCollectionsExternalId);

        ExternalId httpMimeExternalId = externalIdFactory.createMavenExternalId("org.apache.httpcomponents", "httpmime", "4.5.6");
        graphAssert.hasRootDependency(httpMimeExternalId);

        ExternalId commonGradlePluginExternalId = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "common-gradle-plugin", "0.0.+");
        graphAssert.hasRootDependency(commonGradlePluginExternalId);
    }
}
