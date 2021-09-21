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

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class MavenParseDetectableTest extends DetectableFunctionalTest {

    public MavenParseDetectableTest() throws IOException {
        super("mavenparse");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("pom.xml"),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">",
            "    <modelVersion>4.0.0</modelVersion>",
            "    <parent>",
            "        <groupId>com.blackducksoftware.integration</groupId>",
            "        <artifactId>common-maven-parent</artifactId>",
            "        <version>5.0.0</version>",
            "    </parent>",
            "",
            "    <artifactId>hub-teamcity</artifactId>",
            "    <version>4.0.1-SNAPSHOT</version>",
            "    <packaging>pom</packaging>",
            "",
            "    <dependencyManagement>",
            "        <dependencies>",
            "            <dependency>",
            "                <groupId>com.blackducksoftware.integration</groupId>",
            "                <artifactId>hub-common</artifactId>",
            "                <version>1.1.0</version>",
            "            </dependency>",
            "            <dependency>",
            "                <groupId>commons-io</groupId>",
            "                <artifactId>commons-io</artifactId>",
            "                <version>2.6</version>",
            "            </dependency>",
            "        </dependencies>",
            "    </dependencyManagement>",
            "</project>"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createMavenParseDetectable(detectableEnvironment, new MavenParseOptions(false, false));
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size(), "A code location should have been generated.");

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.MAVEN, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId commonsIo = externalIdFactory.createMavenExternalId("commons-io", "commons-io", "2.6");
        ExternalId hubCommon = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "hub-common", "1.1.0");

        graphAssert.hasRootDependency(commonsIo);
        graphAssert.hasRootDependency(hubCommon);
    }
}
