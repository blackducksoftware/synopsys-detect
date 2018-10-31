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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientBomToolResult;

public class DockerBomTool extends BomTool {
    private final DirectoryManager directoryManager;
    private final DockerInspectorManager dockerInspectorManager;
    private final StandardExecutableFinder standardExecutableFinder;
    private final DockerExtractor dockerExtractor;
    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;

    private File javaExe;
    private File bashExe;
    private File dockerExe;
    private String image;
    private String tar;
    private DockerInspectorInfo dockerInspectorInfo;

    public DockerBomTool(final BomToolEnvironment environment, final DirectoryManager directoryManager, final DockerInspectorManager dockerInspectorManager,
        final StandardExecutableFinder standardExecutableFinder, final boolean dockerPathRequired, final String suppliedDockerImage,
        final String suppliedDockerTar, final DockerExtractor dockerExtractor) {
        super(environment, "Docker", BomToolGroupType.DOCKER, BomToolType.DOCKER);
        this.directoryManager = directoryManager;
        this.standardExecutableFinder = standardExecutableFinder;
        this.dockerExtractor = dockerExtractor;
        this.dockerPathRequired = dockerPathRequired;
        this.dockerInspectorManager = dockerInspectorManager;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
    }

    @Override
    public BomToolResult applicable() {
        image = suppliedDockerImage;
        tar = suppliedDockerTar;

        if (StringUtils.isBlank(image) && StringUtils.isBlank(tar)) {
            return new PropertyInsufficientBomToolResult();
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        javaExe = standardExecutableFinder.getExecutable(StandardExecutableType.JAVA);
        if (javaExe == null) {
            return new ExecutableNotFoundBomToolResult("java");
        }

        bashExe = standardExecutableFinder.getExecutable(StandardExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundBomToolResult("bash");
        }

        dockerExe = standardExecutableFinder.getExecutable(StandardExecutableType.DOCKER);
        if (dockerExe == null) {
            if (dockerPathRequired) {
                return new ExecutableNotFoundBomToolResult("docker");
            }
        }

        dockerInspectorInfo = dockerInspectorManager.getDockerInspector();
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundBomToolResult("docker");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        DockerResult result = dockerExtractor.extract(this.getBomToolType(), environment.getDirectory(), outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);
        return result.getExtraction();
    }

}