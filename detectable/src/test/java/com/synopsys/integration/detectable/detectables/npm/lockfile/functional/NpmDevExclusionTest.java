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
package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class NpmDevExclusionTest {
    ExternalId childDev;
    ExternalId parentDev;
    NpmLockfilePackager npmLockfileParser;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        npmLockfileParser = new NpmLockfilePackager(new GsonBuilder().setPrettyPrinting().create(), externalIdFactory);

        packageJsonText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package-lock.json");

        childDev = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "child-dev", "3.0.0");
        parentDev = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "parent-dev", "2.0.0");
    }

    @Test
    public void testDevDependencyNotExists() {
        NpmParseResult result = npmLockfileParser.parse(packageJsonText, packageLockText, false, false);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childDev);
        graphAssert.hasNoDependency(parentDev);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testDevDependencyExists() {
        NpmParseResult result = npmLockfileParser.parse(packageJsonText, packageLockText, true, false);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childDev);
        graphAssert.hasDependency(parentDev);
        graphAssert.hasRootSize(1);
    }
}
