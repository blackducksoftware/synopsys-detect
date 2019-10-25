/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

public class YarnLockExtractor {
    private final YarnListParser yarnListParser;
    private final YarnLockParser yarnLockParser;
    private final YarnLockOptions yarnLockOptions;
    private final ExecutableRunner executableRunner;
    private final YarnTransformer yarnTransformer;

    public YarnLockExtractor(final YarnListParser yarnListParser, final ExecutableRunner executableRunner, final YarnLockParser yarnLockParser, final YarnLockOptions yarnLockOptions, final YarnTransformer yarnTransformer) {
        this.yarnListParser = yarnListParser;
        this.yarnLockParser = yarnLockParser;
        this.executableRunner = executableRunner;
        this.yarnLockOptions = yarnLockOptions;
        this.yarnTransformer = yarnTransformer;
    }

    public Extraction extract(final File directory, final File yarnLockFile, final File packageJsonFile) {
        try {
            final Gson gson = new Gson();

            final PackageJson packageJson = gson.fromJson(FileUtils.readFileToString(packageJsonFile), PackageJson.class);

            final YarnLock yarnLock = yarnLockParser.parseYarnLock(FileUtils.readLines(yarnLockFile));

            final DependencyGraph dependencyGraph = yarnTransformer.transform(packageJson, yarnLock);

            final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
