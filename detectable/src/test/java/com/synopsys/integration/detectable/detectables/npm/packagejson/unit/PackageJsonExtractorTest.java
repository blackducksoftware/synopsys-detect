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
package com.synopsys.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
class PackageJsonExtractorTest {
    private PackageJsonExtractor packageJsonExtractor;

    private ExternalId testDep1;
    private ExternalId testDep2;
    private ExternalId testDevDep1;
    private ExternalId testDevDep2;

    @BeforeEach
    void setUp() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        testDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name1", "version1");
        testDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name2", "version2");
        testDevDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev1", "versionDev1");
        testDevDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev2", "versionDev2");
        packageJsonExtractor = new PackageJsonExtractor(gson, externalIdFactory);
    }

    @Test
    void extractWithNoDevOrPeerDependencies() {
        PackageJson packageJson = createPackageJson();
        Extraction extraction = packageJsonExtractor.extract(packageJson, false, false);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasRootSize(2);
    }

    @Test
    void extractWithDevNoPeerDependencies() {
        PackageJson packageJson = createPackageJson();
        Extraction extraction = packageJsonExtractor.extract(packageJson, true, false);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testDevDep1);
        graphAssert.hasRootDependency(testDevDep2);
        graphAssert.hasRootSize(4);
    }

    private PackageJson createPackageJson() {
        PackageJson packageJson = new PackageJson();

        packageJson.name = "test";
        packageJson.version = "test-version";
        packageJson.dependencies = new HashMap<>();
        packageJson.devDependencies = new HashMap<>();

        packageJson.dependencies.put(testDep1.getName(), testDep1.getVersion());
        packageJson.dependencies.put(testDep2.getName(), testDep2.getVersion());
        packageJson.devDependencies.put(testDevDep1.getName(), testDevDep1.getVersion());
        packageJson.devDependencies.put(testDevDep2.getName(), testDevDep2.getVersion());

        return packageJson;
    }
}
