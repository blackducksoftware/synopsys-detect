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
package com.synopsys.integration.detectable.detectables.gradle.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;

public class GradleBuildFileDetectable extends Detectable {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final FileFinder fileFinder;
    private final BuildGradleParser buildGradleParser;

    private File buildFile;

    public GradleBuildFileDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final BuildGradleParser buildGradleParser) {
        super(environment, "gradle.build", "GRADLE");
        this.fileFinder = fileFinder;
        this.buildGradleParser = buildGradleParser;
    }

    @Override
    public DetectableResult applicable() {
        buildFile = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);

        if (buildFile == null) {
            return new FileNotFoundDetectableResult(BUILD_GRADLE_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try {
            final InputStream buildFileInputStream = new FileInputStream(buildFile);
            final Optional<DependencyGraph> dependencyGraph = buildGradleParser.parse(buildFileInputStream);

            if (dependencyGraph.isPresent()) {
                final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.GRADLE, dependencyGraph.get()).build();
                return new Extraction.Builder().codeLocations(codeLocation).build();
            } else {
                return new Extraction.Builder().failure(String.format("Failed to extract dependencies from %s", BUILD_GRADLE_FILENAME)).build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
