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
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class GradleDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String BUILD_GRADLE_FILENAME = "build.gradle";
    public static final String KOTLIN_BUILD_GRADLE_FILENAME = "build.gradle.kts";

    private final FileFinder fileFinder;
    private final GradleResolver gradleResolver;
    private final GradleInspectorResolver gradleInspectorResolver;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final GradleInspectorOptions gradleInspectorOptions;
    private final GradleTaskChecker gradleTaskChecker;

    private File gradleExe;
    private File gradleInspector;
    private String gradleTask;

    public GradleDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GradleResolver gradleResolver, final GradleInspectorResolver gradleInspectorResolver,
        final GradleInspectorExtractor gradleInspectorExtractor, final GradleInspectorOptions gradleInspectorOptions, final GradleTaskChecker gradleTaskChecker) {
        super(environment, "Gradle", "Gradle");
        this.fileFinder = fileFinder;
        this.gradleResolver = gradleResolver;
        this.gradleInspectorResolver = gradleInspectorResolver;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.gradleInspectorOptions = gradleInspectorOptions;
        this.gradleTaskChecker = gradleTaskChecker;
    }

    @Override
    public DetectableResult applicable() {
        final File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle != null) {
            return new PassedDetectableResult();
        }

        final File kotlinBuildGradle = fileFinder.findFile(environment.getDirectory(), KOTLIN_BUILD_GRADLE_FILENAME);
        if (kotlinBuildGradle != null) {
            return new PassedDetectableResult();
        }

        return new FilesNotFoundDetectableResult(BUILD_GRADLE_FILENAME, KOTLIN_BUILD_GRADLE_FILENAME);
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        gradleExe = gradleResolver.resolveGradle(environment);
        if (gradleExe == null) {
            return new ExecutableNotFoundDetectableResult("gradle");
        }

        gradleInspector = gradleInspectorResolver.resolveGradleInspector();
        if (gradleInspector == null) {
            return new InspectorNotFoundDetectableResult("gradle");
        }

        gradleTask = gradleTaskChecker.getGoGradleTask(environment.getDirectory(), gradleExe).orElse(null);

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        final Extraction gradleInspectorExtraction = gradleInspectorExtractor.extract(environment.getDirectory(), gradleExe, gradleInspectorOptions.getGradleBuildCommand(), gradleInspector, extractionEnvironment.getOutputDirectory());
        if (gradleTask != null) {

        }
        return gradleInspectorExtraction;
    }
}
