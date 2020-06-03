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
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

public class YarnLockExtractor {
    private final YarnPackager yarnPackager;
    private final YarnTransformer yarnTransformer;
    private final YarnLockOptions yarnLockOptions;

    public YarnLockExtractor(YarnTransformer yarnTransformer, YarnPackager yarnPackager, YarnLockOptions yarnLockOptions) {
        this.yarnTransformer = yarnTransformer;
        this.yarnPackager = yarnPackager;
        this.yarnLockOptions = yarnLockOptions;
    }

    public Extraction extract(File yarnLockFile, File packageJsonFile) {
        try {
            YarnLockResult yarnLockResult = yarnPackager.generateYarnResult(packageJsonFile, yarnLockFile);
            DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, yarnLockOptions.useProductionOnly());

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder()
                       .projectName(yarnLockResult.getPackageJson().name)
                       .projectVersion(yarnLockResult.getPackageJson().version)
                       .success(codeLocation)
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
