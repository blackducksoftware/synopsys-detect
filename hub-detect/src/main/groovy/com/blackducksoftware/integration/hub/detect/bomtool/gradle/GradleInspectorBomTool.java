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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.manager.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class GradleInspectorBomTool extends BomTool {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final DetectFileFinder fileFinder;
    private final GradleExecutableFinder gradleFinder;
    private final GradleInspectorManager gradleInspectorManager;
    private final GradleInspectorExtractor gradleInspectorExtractor;

    private String gradleExe;
    private String gradleInspector;

    public GradleInspectorBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final GradleExecutableFinder gradleFinder, final GradleInspectorManager gradleInspectorManager,
            final GradleInspectorExtractor gradleInspectorExtractor) {
        super(environment, "Gradle Inspector", BomToolGroupType.GRADLE, BomToolType.GRADLE_INSPECTOR);
        this.fileFinder = fileFinder;
        this.gradleFinder = gradleFinder;
        this.gradleInspectorManager = gradleInspectorManager;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle == null) {
            return new FileNotFoundBomToolResult(BUILD_GRADLE_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        gradleExe = gradleFinder.findGradle(environment);
        if (gradleExe == null) {
            return new ExecutableNotFoundBomToolResult("gradle");
        }

        gradleInspector = gradleInspectorManager.getGradleInspector(environment);
        if (gradleInspector == null) {
            return new InspectorNotFoundBomToolResult("gradle");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return gradleInspectorExtractor.extract(this.getBomToolType(), environment.getDirectory(), gradleExe, gradleInspector, extractionId);
    }

}
