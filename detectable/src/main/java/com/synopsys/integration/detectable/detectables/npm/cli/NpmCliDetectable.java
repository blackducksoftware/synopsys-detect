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
package com.synopsys.integration.detectable.detectables.npm.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.NpmRunInstallDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class NpmCliDetectable extends Detectable {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmResolver npmResolver;
    private final NpmCliExtractor npmCliExtractor;

    private File npmExe;

    public NpmCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final NpmResolver npmResolver, final NpmCliExtractor npmCliExtractor) {
        super(environment, "Npm Cli", "NPM");
        this.fileFinder = fileFinder;
        this.npmResolver = npmResolver;
        this.npmCliExtractor = npmCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        final File packageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);

        if (packageJson == null) {
            return new FileNotFoundDetectableResult(PACKAGE_JSON);
        }

        // addRelevantDiagnosticFile(packageJson); // TODO: Jordan fix me
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        final File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmRunInstallDetectableResult(environment.getDirectory().getAbsolutePath());
        }

        npmExe = npmResolver.resolveNpm(environment);
        if (npmExe == null) {
            return new ExecutableNotFoundDetectableResult("npm");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return npmCliExtractor.extract(environment.getDirectory(), npmExe);
    }

}
