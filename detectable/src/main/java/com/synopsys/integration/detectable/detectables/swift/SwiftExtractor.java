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
package com.synopsys.integration.detectable.detectables.swift;

import java.io.File;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class SwiftExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final SwiftCliParser swiftCliParser;
    private final SwiftPackageTransformer swiftPackageTransformer;

    public SwiftExtractor(final DetectableExecutableRunner executableRunner, final SwiftCliParser swiftCliParser, final SwiftPackageTransformer swiftPackageTransformer) {
        this.executableRunner = executableRunner;
        this.swiftCliParser = swiftCliParser;
        this.swiftPackageTransformer = swiftPackageTransformer;
    }

    public Extraction extract(final File environmentDirectory, final File swiftExecutable) {
        try {
            final SwiftPackage rootSwiftPackage = getRootSwiftPackage(environmentDirectory, swiftExecutable);
            final CodeLocation codeLocation = swiftPackageTransformer.transform(rootSwiftPackage);

            return new Extraction.Builder()
                       .success(codeLocation)
                       .projectName(rootSwiftPackage.getName())
                       .projectVersion(rootSwiftPackage.getVersion())
                       .build();
        } catch (final IntegrationException | ExecutableRunnerException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private SwiftPackage getRootSwiftPackage(final File environmentDirectory, final File swiftExecutable) throws ExecutableRunnerException, IntegrationException {
        final ExecutableOutput executableOutput = executableRunner.execute(environmentDirectory, swiftExecutable, "package", "show-dependencies", "--format", "json");
        if (executableOutput.getReturnCode() == 0) {
            return swiftCliParser.parseOutput(executableOutput.getStandardOutputAsList());
        } else {
            throw new IntegrationException(String.format("Swift returned a non-zero exit code (%d). Failed to parse output.", executableOutput.getReturnCode()));
        }
    }

}
