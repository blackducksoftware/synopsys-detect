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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.tool.SimpleToolDetector;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder.CacheableExecutableType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.InspectorNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.WrongOperatingSystemResult;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.util.NameVersion;

public class DockerDetector implements SimpleToolDetector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectInfo detectInfo;
    private final DetectorEnvironment environment;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;
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

    public DockerDetector(final DetectInfo detectInfo, final DetectorEnvironment environment, final DirectoryManager directoryManager, final EventSystem eventSystem, final DockerInspectorManager dockerInspectorManager,
        final CacheableExecutableFinder cacheableExecutableFinder, final boolean dockerPathRequired, final String suppliedDockerImage,
        final String suppliedDockerTar, final DockerExtractor dockerExtractor) {
        this.detectInfo = detectInfo;
        this.environment = environment;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
        this.dockerExtractor = dockerExtractor;
        this.dockerPathRequired = dockerPathRequired;
        this.dockerInspectorManager = dockerInspectorManager;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
    }

    @Override
    public String getName() {
        return "Docker";
    }

    @Override
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

    @Override
    public DetectorResult extractable() throws DetectorException {
        javaExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.JAVA);
        if (javaExe == null) {
            return new ExecutableNotFoundDetectorResult("java");
        }

        bashExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundDetectorResult("bash");
        }

        try {
            dockerExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.DOCKER);
        } catch (Exception e) {
            dockerExe = null;
        }
        if (dockerExe == null) {
            if (dockerPathRequired) {
                return new ExecutableNotFoundDetectorResult("docker");
            } else {
                logger.info("Docker executable not found, but it has been configured as not-required; proceeding with execution of Docker tool");
            }
        }

        dockerInspectorInfo = dockerInspectorManager.getDockerInspector();
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundDetectorResult("docker");
        }

        return new PassedDetectorResult();
    }

    @Override
    public void extract(final EventSystem eventSystem, final DetectorResult extractableResult, final RunResult runResult) {
        if (extractableResult.getPassed()) {
            logger.info("Performing the Docker extraction.");
            Extraction extractResult = dockerExtractor.extract(environment.getDirectory(), directoryManager.getDockerOutputDirectory(), bashExe, javaExe, image, tar, dockerInspectorInfo);
            if (StringUtils.isNotBlank(extractResult.projectName) && StringUtils.isNotBlank(extractResult.projectVersion)) {
                runResult.addToolNameVersionIfPresent(DetectTool.DOCKER, Optional.of(new NameVersion(extractResult.projectName, extractResult.projectVersion)));
            }
            Optional<Object> dockerTar = extractResult.getMetaDataValue(DockerExtractor.DOCKER_TAR_META_DATA_KEY);
            if (dockerTar.isPresent()) {
                runResult.addDockerFile(Optional.of((File) dockerTar.get()));
            }
            runResult.addDetectCodeLocations(extractResult.codeLocations);
            if (extractResult.result == Extraction.ExtractionResultType.SUCCESS) {
                eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.SUCCESS));
            } else {
                eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
            }
        } else {
            logger.error(String.format("Docker was not extractable: %s", extractableResult.toDescription()));
            eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
        }
    }

}