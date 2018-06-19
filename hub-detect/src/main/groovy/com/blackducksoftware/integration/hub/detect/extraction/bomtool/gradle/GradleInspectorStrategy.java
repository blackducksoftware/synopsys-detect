/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import java.io.File;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GradleInspectorStrategy extends Strategy {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    DetectFileFinder fileFinder;
    GradleExecutableFinder gradleFinder;
    GradleInspectorManager gradleInspectorManager;
    GradleInspectorExtractor gradleInspectorExtractor;

    private String gradleExe;
    private String gradleInspector;

    public GradleInspectorStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final GradleExecutableFinder gradleFinder, final GradleInspectorManager gradleInspectorManager, final GradleInspectorExtractor gradleInspectorExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gradleFinder = gradleFinder;
        this.gradleInspectorManager = gradleInspectorManager;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
    }

    @Override
    public StrategyResult applicable() {
        final File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle == null) {
            return new FileNotFoundStrategyResult(BUILD_GRADLE_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        gradleExe = gradleFinder.findGradle(environment);
        if (gradleExe == null) {
            return new ExecutableNotFoundStrategyResult("gradle");
        }

        gradleInspector = gradleInspectorManager.getGradleInspector(environment);
        if (gradleInspector == null) {
            return new InspectorNotFoundStrategyResult("gradle");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return gradleInspectorExtractor.extract(environment.getDirectory(), gradleExe, gradleInspector, extractionId);
    }

    @Override
    public String getName() {
        return "Gradle Inspector";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GRADLE;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.GRADLE_INSPECTOR;
    }


}