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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PropertyInsufficientStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;

@Component
public class DockerStrategy extends Strategy {
    private final DockerInspectorManager dockerInspectorManager;
    private final StandardExecutableFinder standardExecutableFinder;
    private final DockerExtractor dockerExtractor;
    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;

    private File bashExe;
    private File dockerExe;
    private String image;
    private String tar;
    private DockerInspectorInfo dockerInspectorInfo;

    public DockerStrategy(final StrategyEnvironment environment, final DockerInspectorManager dockerInspectorManager, final StandardExecutableFinder standardExecutableFinder, final boolean dockerPathRequired, final String suppliedDockerImage, final String suppliedDockerTar, final DockerExtractor dockerExtractor) {
        super(environment);
        this.standardExecutableFinder = standardExecutableFinder;
        this.dockerExtractor = dockerExtractor;
        this.dockerPathRequired = dockerPathRequired;
        this.dockerInspectorManager = dockerInspectorManager;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
    }

    @Override
    public StrategyResult applicable() {
        image = suppliedDockerImage;
        tar = suppliedDockerTar;

        if (StringUtils.isBlank(image) && StringUtils.isBlank(tar)) {
            return new PropertyInsufficientStrategyResult();
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        bashExe = standardExecutableFinder.getExecutable(StandardExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundStrategyResult("bash");
        }

        dockerExe = standardExecutableFinder.getExecutable(StandardExecutableType.DOCKER);
        if (dockerExe == null) {
            if (dockerPathRequired) {
                return new ExecutableNotFoundStrategyResult("docker");
            }
        }

        dockerInspectorInfo = dockerInspectorManager.getDockerInspector(environment);
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundStrategyResult("docker");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return dockerExtractor.extract(environment.getDirectory(), extractionId, bashExe, dockerExe, image, tar, dockerInspectorInfo);
    }

    @Override
    public String getName() {
        return "Docker";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.DOCKER;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.DOCKER;
    }

}