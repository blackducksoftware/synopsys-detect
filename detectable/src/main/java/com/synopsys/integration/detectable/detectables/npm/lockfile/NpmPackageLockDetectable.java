/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class NpmPackageLockDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmLockfileExtractor npmLockfileExtractor;
    private final boolean includeDevDependencies;

    private File lockfile;
    private File packageJson;

    public NpmPackageLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final NpmLockfileExtractor npmLockfileExtractor, final boolean includeDevDependencies) {
        super(environment, "Package Lock", "NPM");
        this.fileFinder = fileFinder;
        this.npmLockfileExtractor = npmLockfileExtractor;
        this.includeDevDependencies = includeDevDependencies;
    }

    @Override
    public DetectableResult applicable() {
        lockfile = fileFinder.findFile(environment.getDirectory(), PACKAGE_LOCK_JSON);
        if (lockfile == null) {
            return new FileNotFoundDetectableResult(PACKAGE_LOCK_JSON);
        }

        final File foundPackageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        if (foundPackageJson == null) {
            logger.warn("Npm applied but it could not find a package.json so dependencies may not be entirely accurate.");
        } else {
            packageJson = foundPackageJson;
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        //        addRelevantDiagnosticFile(lockfile); // TODO: Jordan FIX ME
        //        if (packageJson != null) {
        //            addRelevantDiagnosticFile(packageJson);
        //        }
        return npmLockfileExtractor.extract(environment.getDirectory(), lockfile, packageJson, includeDevDependencies);
    }

}
