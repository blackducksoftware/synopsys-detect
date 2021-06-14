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
public class NpmPeerExclusionTest {
    ExternalId childPeer;
    ExternalId parentPeer;
    NpmLockfilePackager npmLockfileParser;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        npmLockfileParser = new NpmLockfilePackager(new GsonBuilder().setPrettyPrinting().create(), externalIdFactory);

        packageJsonText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package-lock.json");

        childPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "child-peer", "3.0.0");
        parentPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "parent-peer", "2.0.0");
    }

    @Test
    public void testPeerDependencyNotExists() {
        NpmParseResult result = npmLockfileParser.parse(packageJsonText, packageLockText, false, false);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childPeer);
        graphAssert.hasNoDependency(parentPeer);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testPeerDependencyExists() {
        NpmParseResult result = npmLockfileParser.parse(packageJsonText, packageLockText, false, true);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childPeer);
        graphAssert.hasDependency(parentPeer);
        graphAssert.hasRootSize(1);
    }
}
