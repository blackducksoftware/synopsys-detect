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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder.CacheableExecutableType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.InspectorNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.WrongOperatingSystemResult;

public class DockerDetector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectInfo detectInfo;
    private final DetectorEnvironment environment;
    private final DirectoryManager directoryManager;
    private final DockerInspectorManager dockerInspectorManager;
    private final CacheableExecutableFinder cacheableExecutableFinder;
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

    public DockerDetector(final DetectInfo detectInfo, final DetectorEnvironment environment, final DirectoryManager directoryManager, final DockerInspectorManager dockerInspectorManager,
        final CacheableExecutableFinder cacheableExecutableFinder, final boolean dockerPathRequired, final String suppliedDockerImage,
        final String suppliedDockerTar, final DockerExtractor dockerExtractor) {
        this.detectInfo = detectInfo;
        this.environment = environment;
        this.directoryManager = directoryManager;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
        this.dockerExtractor = dockerExtractor;
        this.dockerPathRequired = dockerPathRequired;
        this.dockerInspectorManager = dockerInspectorManager;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
    }

    public DetectorResult applicable() {
        if (detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS) {
            return new WrongOperatingSystemResult(detectInfo.getCurrentOs());
        }
        image = suppliedDockerImage;
        tar = suppliedDockerTar;

        if (StringUtils.isBlank(image) && StringUtils.isBlank(tar)) {
            return new PropertyInsufficientDetectorResult();
        }
        return new PassedDetectorResult();
    }

    public DetectorResult extractable() throws DetectorException {
        javaExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.JAVA);
        if (javaExe == null) {
            return new ExecutableNotFoundDetectorResult("java");
        }

        bashExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundDetectorResult("bash");
        }

        dockerExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.DOCKER);
        if (dockerExe == null) {
            if (dockerPathRequired) {
                return new ExecutableNotFoundDetectorResult("docker");
            }
        }

        dockerInspectorInfo = dockerInspectorManager.getDockerInspector();
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundDetectorResult("docker");
        }

        return new PassedDetectorResult();
    }

    public Extraction extract(final File outputDirectory) {
        return dockerExtractor.extract(environment.getDirectory(), outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);
    }

}