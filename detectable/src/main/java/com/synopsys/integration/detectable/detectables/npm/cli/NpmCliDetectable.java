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
package com.synopsys.integration.detectable.detectables.npm.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.NpmNodeModulesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.npm.NpmPackageJsonDiscoverer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: node_modules, package.json. <br /><br /> Executable: npm.")
public class NpmCliDetectable extends Detectable {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmResolver npmResolver;
    private final NpmCliExtractor npmCliExtractor;
    private final NpmPackageJsonDiscoverer npmPackageJsonDiscoverer;
    private final NpmCliExtractorOptions npmCliExtractorOptions;

    private File packageJson;
    private File npmExe;

    public NpmCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final NpmResolver npmResolver, final NpmCliExtractor npmCliExtractor, final NpmPackageJsonDiscoverer npmPackageJsonDiscoverer,
        NpmCliExtractorOptions npmCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmResolver = npmResolver;
        this.npmCliExtractor = npmCliExtractor;
        this.npmPackageJsonDiscoverer = npmPackageJsonDiscoverer;
        this.npmCliExtractorOptions = npmCliExtractorOptions; // TODO: Should this be wrapped in a detectables option?
    }

    @Override
    public Discovery discover(final ExtractionEnvironment extractionEnvironment) {
        return npmPackageJsonDiscoverer.discover(packageJson);
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageJson = requirements.file(PACKAGE_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        final File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmNodeModulesNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }

        npmExe = npmResolver.resolveNpm(environment);
        if (npmExe == null) {
            return new ExecutableNotFoundDetectableResult("npm");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return npmCliExtractor.extract(environment.getDirectory(), npmExe, npmCliExtractorOptions);
    }

}
