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
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;

import javax.naming.spi.DirectoryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorTemplateResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;

public class GradleInspectorDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final FileFinder fileFinder;
    private final GradleResolver gradleResolver;
    private final GradleInspectorTemplateResolver gradleInspectorTemplateResolver;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final GradleInspectorOptions gradleInspectorOptions;
    private final GradleInspectorScriptCreator gradleInspectorScriptCreator;

    private File gradleExe;
    private File gradleInspector;

    public GradleInspectorDetectable(final DetectableEnvironment environment, final FileFinder fileFinder,
        final GradleResolver gradleResolver, final GradleInspectorTemplateResolver gradleInspectorTemplateResolver,
        final GradleInspectorExtractor gradleInspectorExtractor, final GradleInspectorOptions gradleInspectorOptions,
        final GradleInspectorScriptCreator gradleInspectorScriptCreator) {
        super(environment, "Gradle Inspector", "Gradle");
        this.fileFinder = fileFinder;
        this.gradleResolver = gradleResolver;
        this.gradleInspectorTemplateResolver = gradleInspectorTemplateResolver;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.gradleInspectorOptions = gradleInspectorOptions;
        this.gradleInspectorScriptCreator = gradleInspectorScriptCreator;
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
        gradleExe = gradleResolver.resolveGradle(environment);
        if (gradleExe == null) {
            return new ExecutableNotFoundDetectableResult("gradle");
        }

        final File gradleInspectorTemplate = gradleInspectorTemplateResolver.resolveGradleInspectorTemplate();
        if (gradleInspectorTemplate == null) {
            return new InspectorNotFoundDetectableResult("gradle");
        }

        gradleInspector = gradleInspectorScriptCreator.createGradleInspector(gradleInspectorTemplate, gradleInspectorOptions.getGradleInspectorScriptOptions());
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
