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
package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "File: package.json.")
public class NpmPackageJsonParseDetectable extends Detectable {
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final PackageJsonExtractor packageJsonExtractor;
    private final boolean includeDevDependencies;

    private File packageJsonFile;

    public NpmPackageJsonParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PackageJsonExtractor packageJsonExtractor,
        final NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageJsonExtractor = packageJsonExtractor;
        this.includeDevDependencies = npmPackageJsonParseDetectableOptions.shouldIncludeDevDependencies();
    }

    @Override
    public DetectableResult applicable() {
        packageJsonFile = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);

        if (packageJsonFile == null) {
            return new FileNotFoundDetectableResult(PACKAGE_JSON);
        } else {
            relevantFiles.add(packageJsonFile);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream packageJsonInputStream = new FileInputStream(packageJsonFile)) {
            return packageJsonExtractor.extract(packageJsonInputStream, includeDevDependencies);
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", PACKAGE_JSON)).build();
        }
    }
}
