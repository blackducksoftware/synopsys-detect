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
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final FileFinder fileFinder;
    private final GradleInspectorDetectableOptions options;
    //private final GradleExecutableFinder gradleFinder;
    //private final GradleInspectorManager gradleInspectorManager;
    //private final GradleInspectorExtractor gradleInspectorExtractor;

    private String gradleExe;
    private String gradleInspector;
    private LocalExecutableFinder localExecutableFinder;
    private SystemExecutableFinder systemExecutableFinder;

    public GradleInspectorDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, GradleInspectorDetectableOptions options
        //final GradleExecutableFinder gradleFinder, final GradleInspectorManager gradleInspectorManager,
        //final GradleInspectorExtractor gradleInspectorExtractor
    ) {
        super(environment, "Gradle Inspector", "Gradle");

        this.fileFinder = fileFinder;
        this.options = options;
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
        if (options.gradleExe.isPresent()) {
            gradleExe = options.gradleExe.get();
        } else {
            gradleExe = localExecutableFinder.findExecutable("gradlew", environment.getDirectory()).getAbsolutePath();
            if (gradleExe == null) {
                gradleExe = systemExecutableFinder.findExecutable("gradle").getAbsolutePath();
            }
        }

        if (gradleExe == null) {
            return new ExecutableNotFoundDetectableResult("gradle");
        }

        //gradleInspector = gradleInspectorManager.getGradleInspector();
        if (gradleInspector == null) {
            return new InspectorNotFoundDetectableResult("gradle");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        //File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        //return gradleInspectorExtractor.extract(environment.getDirectory(), gradleExe, gradleInspector, outputDirectory);
        return null;
    }

}
