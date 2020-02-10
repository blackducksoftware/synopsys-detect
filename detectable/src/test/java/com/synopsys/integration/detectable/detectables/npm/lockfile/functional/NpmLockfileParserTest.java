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

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileParser;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class NpmLockfileParserTest {
    private NpmLockfileParser npmLockfileParser;

    @BeforeEach
    public void init() {
        npmLockfileParser = new NpmLockfileParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    @Disabled
    public void parseLockFileWithRecreatedJsonTest() {
        final String lockFileText = FunctionalTestFiles.asString("/npm/package-lock.json");

        final NpmParseResult result = npmLockfileParser.parse(recreatePackageJsonFromLock(lockFileText), lockFileText, true);

        Assertions.assertEquals("knockout-tournament", result.getProjectName());
        Assertions.assertEquals("1.0.0", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/packageLockExpected_graph.json", result.getCodeLocation().getDependencyGraph());
    }

    @Test
    @Disabled
    public void parseLockFileTest() {
        final String lockFileText = FunctionalTestFiles.asString("/npm/package-lock.json");

        final NpmParseResult result = npmLockfileParser.parse(Optional.empty(), lockFileText, true);

        Assertions.assertEquals("knockout-tournament", result.getProjectName());
        Assertions.assertEquals("1.0.0", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/packageLockExpected_graph.json", result.getCodeLocation().getDependencyGraph());
    }

    private Optional<String> recreatePackageJsonFromLock(final String lockFileText) {
        //These tests were written before we needed a package json.
        //So we replicate a package json with every package as root.
        final PackageJson packageJson = new PackageJson();
        final Gson gson = new Gson();
        final PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        packageLock.dependencies.forEach((key, value) -> packageJson.dependencies.put(key, key));
        final String text = gson.toJson(packageJson);
        return Optional.of(text);
    }

    @Test
    @Disabled
    public void parseShrinkwrapWithRecreatedJsonTest() {
        final String shrinkwrapText = FunctionalTestFiles.asString("/npm/npm-shrinkwrap.json");
        final NpmParseResult result = npmLockfileParser.parse(recreatePackageJsonFromLock(shrinkwrapText), shrinkwrapText, true);

        Assertions.assertEquals("fec-builder", result.getProjectName());
        Assertions.assertEquals("1.3.7", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/shrinkwrapExpected_graph.json", result.getCodeLocation().getDependencyGraph());
    }

    @Test
    @Disabled
    public void parseShrinkwrapTest() {
        final String shrinkwrapText = FunctionalTestFiles.asString("/npm/npm-shrinkwrap.json");
        final NpmParseResult result = npmLockfileParser.parse(Optional.empty(), shrinkwrapText, true);

        Assertions.assertEquals("fec-builder", result.getProjectName());
        Assertions.assertEquals("1.3.7", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/shrinkwrapExpected_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
