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
package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class MavenPomWrapperBomTool extends BomTool {
    public static final String POM_WRAPPER_FILENAME = "pom.groovy";

    private final DetectFileFinder fileFinder;
    private final MavenExecutableFinder mavenExecutableFinder;
    private final MavenCliExtractor mavenCliExtractor;

    private String mavenExe;

    public MavenPomWrapperBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final MavenExecutableFinder mavenExecutableFinder, final MavenCliExtractor mavenCliExtractor) {
        super(environment, "Pom wrapper file", BomToolGroupType.MAVEN, BomToolType.MAVEN_POM_WRAPPER_CLI);
        this.fileFinder = fileFinder;
        this.mavenExecutableFinder = mavenExecutableFinder;
        this.mavenCliExtractor = mavenCliExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File pom = fileFinder.findFile(environment.getDirectory(), POM_WRAPPER_FILENAME);
        if (pom == null) {
            return new FileNotFoundBomToolResult(POM_WRAPPER_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() {
        mavenExe = mavenExecutableFinder.findMaven(environment);

        if (mavenExe == null) {
            return new ExecutableNotFoundBomToolResult("mvn");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return mavenCliExtractor.extract(this.getBomToolType(), environment.getDirectory(), mavenExe);
    }

}
