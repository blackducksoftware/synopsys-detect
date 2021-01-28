/**
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
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParserNew;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;
import com.synopsys.integration.util.NameVersion;

public class YarnPackager {
    private final Gson gson;
    private final YarnLockParserNew yarnLockParser;
    private final YarnTransformer yarnTransformer;
    private final YarnLockOptions yarnLockOptions;

    public YarnPackager(Gson gson, YarnLockParserNew yarnLockParser, YarnTransformer yarnTransformer, YarnLockOptions yarnLockOptions) {
        this.gson = gson;
        this.yarnLockParser = yarnLockParser;
        this.yarnTransformer = yarnTransformer;
        this.yarnLockOptions = yarnLockOptions;
    }

    public YarnResult generateYarnResult(String packageJsonText, List<String> yarnLockLines, String yarnLockFilePath, List<NameVersion> externalDependencies) {
        PackageJson packageJson = gson.fromJson(packageJsonText, PackageJson.class);
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, yarnLockFilePath, yarnLock);

        try {
            DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, yarnLockOptions.useProductionOnly(), externalDependencies);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return YarnResult.success(packageJson.name, packageJson.version, codeLocation);
        } catch (MissingExternalIdException exception) {
            return YarnResult.failure(exception);
        }
    }
}
