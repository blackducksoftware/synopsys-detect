/**
 * hub-detect
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
package com.synopsys.integration.detectable.detectables.gradle;

import java.io.File;

import javax.naming.spi.DirectoryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.LocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class GradleInspectorDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final LocalExecutableFinder localExecutableFinder;
    private final SystemExecutableFinder systemExecutableFinder;
    private final GradleInspectorResolver gradleInspectorResolver;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final GradleInspectorOptions gradleInspectorOptions;

    private File gradleExe;
    private File gradleInspector;

    public GradleInspectorDetectable(final DetectableEnvironment environment, final DirectoryManager directoryManager, final FileFinder fileFinder,
        final LocalExecutableFinder localExecutableFinder, SystemExecutableFinder systemExecutableFinder, final GradleInspectorResolver gradleInspectorResolver,
        final GradleInspectorExtractor gradleInspectorExtractor, GradleInspectorOptions gradleInspectorOptions) {
        super(environment, "Gradle Inspector", "Gradle");
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.localExecutableFinder = localExecutableFinder;
        this.systemExecutableFinder = systemExecutableFinder;
        this.gradleInspectorResolver = gradleInspectorResolver;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.gradleInspectorOptions = gradleInspectorOptions;
    }

    @Override
    public DetectableResult applicable() {
        final File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle == null) {
            return new FileNotFoundDetectableResult(BUILD_GRADLE_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        gradleExe = localExecutableFinder.findExecutable("gradlew", environment.getDirectory());
        if (gradleExe != null) {
            gradleExe = systemExecutableFinder.findExecutable("gradle");
        }
        if (gradleExe == null) {
            return new ExecutableNotFoundDetectableResult("gradle");
        }

        //gradleInspector = gradleInspectorResolver.getGradleInspector();
        if (gradleInspector == null) {
            return new InspectorNotFoundDetectableResult("gradle");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        //File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return gradleInspectorExtractor.extract(environment.getDirectory(), gradleExe, gradleInspectorOptions.getGradleBuildCommand(), gradleInspector, extractionEnvironment.getOutputDirectory());
    }

}
