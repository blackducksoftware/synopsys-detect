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
package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "File: package-lock.json. Optionally for better results: package.json also.")
public class NpmPackageLockDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmLockfileExtractor npmLockfileExtractor;
    private final boolean includeDevDependencies;

    private File lockfile;
    private File packageJson;

    public NpmPackageLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final NpmLockfileExtractor npmLockfileExtractor, final NpmLockfileOptions npmLockfileOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmLockfileExtractor = npmLockfileExtractor;
        this.includeDevDependencies = npmLockfileOptions.shouldIncludeDeveloperDependencies();
    }

    @Override
    public DetectableResult applicable() {
        Requirements requires = new Requirements();
        lockfile = requires.file(fileFinder, environment, PACKAGE_LOCK_JSON);
        packageJson = requires.file(fileFinder, environment, PACKAGE_JSON);
        return requires.result();
        //
        //        lockfile = fileFinder.findFile(environment.getDirectory(), PACKAGE_LOCK_JSON);
        //        if (lockfile == null) {
        //            return new FileNotFoundDetectableResult(PACKAGE_LOCK_JSON);
        //        } else {
        //            relevantFiles.add(lockfile);
        //        }
        //
        //        packageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        //        if (packageJson == null) {
        //            logger.warn("Npm applied but it could not find a package.json so dependencies may not be entirely accurate.");
        //        } else {
        //            relevantFiles.add(packageJson);
        //        }
        //
        //        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return npmLockfileExtractor.extract(lockfile, packageJson, includeDevDependencies);
    }

}
