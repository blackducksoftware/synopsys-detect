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
package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;

public class LernaExtractor {
    private final LernaPackageDiscoverer lernaPackageDiscoverer;
    private final LernaPackager lernaPackager;

    public LernaExtractor(LernaPackageDiscoverer lernaPackageDiscoverer, LernaPackager lernaPackager) {
        this.lernaPackageDiscoverer = lernaPackageDiscoverer;
        this.lernaPackager = lernaPackager;
    }

    public Extraction extract(File sourceDirectory, File packageJson, File lernaExecutable) {
        try {
            List<LernaPackage> lernaPackages = lernaPackageDiscoverer.discoverLernaPackages(sourceDirectory, lernaExecutable);
            LernaResult lernaResult = lernaPackager.generateLernaResult(sourceDirectory, packageJson, lernaPackages);

            if (lernaResult.getException().isPresent()) {
                throw lernaResult.getException().get();
            }

            return new Extraction.Builder()
                       .projectName(lernaResult.getProjectName())
                       .projectVersion(lernaResult.getProjectVersionName())
                       .success(lernaResult.getCodeLocations())
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
