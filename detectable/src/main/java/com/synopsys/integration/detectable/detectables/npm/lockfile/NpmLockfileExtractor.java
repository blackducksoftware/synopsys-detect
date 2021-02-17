/*
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
package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;

public class NpmLockfileExtractor {
    private final NpmLockfilePackager npmLockfileParser;

    public NpmLockfileExtractor(NpmLockfilePackager npmLockfileParser) {
        this.npmLockfileParser = npmLockfileParser;
    }

    /*
    packageJson is optional
     */
    public Extraction extract(File lockfile, File packageJson, boolean includeDevDependencies) {
        try {
            String lockText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            String packageText = null;
            if (packageJson != null) {
                packageText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            }

            NpmParseResult result = npmLockfileParser.parse(packageText, lockText, includeDevDependencies);

            return new Extraction.Builder()
                       .success(result.getCodeLocation())
                       .projectName(result.getProjectName())
                       .projectVersion(result.getProjectVersion())
                       .build();

        } catch (IOException e) {
            return new Extraction.Builder()
                       .exception(e)
                       .build();
        }
    }
}
