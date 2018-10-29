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

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder.StandardExecutableType;

public class DockerTool {
    private final DockerInspectorManager dockerInspectorManager;
    private final StandardExecutableFinder standardExecutableFinder;
    private final DockerExtractor dockerExtractor;
    private final DockerOptions dockerBomToolOptions;

    private File javaExe;
    private File bashExe;
    private File dockerExe;
    private String image;
    private String tar;
    private DockerInspectorInfo dockerInspectorInfo;

    public DockerTool(final DockerInspectorManager dockerInspectorManager, final StandardExecutableFinder standardExecutableFinder, final DockerExtractor dockerExtractor,
        DockerOptions options) {
        this.standardExecutableFinder = standardExecutableFinder;
        this.dockerExtractor = dockerExtractor;
        this.dockerInspectorManager = dockerInspectorManager;
        this.dockerBomToolOptions = options;
    }

    public boolean shouldRun() {
        image = dockerBomToolOptions.getSuppliedDockerImage();
        tar = dockerBomToolOptions.getSuppliedDockerTar();

        if (StringUtils.isBlank(image) && StringUtils.isBlank(tar)) {
            return false;
        }

        return true;
    }

    public DockerResult run(File sourcePath, File outputDirectory) throws DetectUserFriendlyException, BomToolException {
        javaExe = standardExecutableFinder.getExecutable(StandardExecutableType.JAVA);
        if (javaExe == null) {
            throw new DetectUserFriendlyException("Docker requires java to run.", ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        bashExe = standardExecutableFinder.getExecutable(StandardExecutableType.BASH);
        if (bashExe == null) {
            throw new DetectUserFriendlyException("Docker requires bash to run.", ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        dockerExe = standardExecutableFinder.getExecutable(StandardExecutableType.DOCKER);
        if (dockerExe == null) {
            if (dockerBomToolOptions.isDockerPathRequired()) {
                throw new DetectUserFriendlyException("Docker requires docker to run.", ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }

        dockerInspectorInfo = dockerInspectorManager.getDockerInspector();
        if (dockerInspectorInfo == null) {
            throw new DetectUserFriendlyException("Docker requires the docker inspector to run.", ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        return dockerExtractor.extract(BomToolType.DOCKER, sourcePath, outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);

    }

}
