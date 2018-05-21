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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PropertyInsufficientStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class DockerStrategy extends Strategy<DockerContext, DockerExtractor> {
    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DockerInspectorManager dockerInspectorManager;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public DockerStrategy() {
        super("Docker", BomToolType.DOCKER, DockerContext.class, DockerExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final DockerContext context) {
        context.image = detectConfiguration.getDockerImage();
        context.tar = detectConfiguration.getDockerTar();

        if (StringUtils.isBlank(context.image) && StringUtils.isBlank(context.tar)) {
            return new PropertyInsufficientStrategyResult();
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final DockerContext context) throws StrategyException {
        context.bashExe = standardExecutableFinder.getExecutable(StandardExecutableType.BASH);
        if (context.bashExe == null) {
            return new ExecutableNotFoundStrategyResult("bash");
        }

        context.dockerExe = standardExecutableFinder.getExecutable(StandardExecutableType.DOCKER);
        if (context.dockerExe == null) {
            return new ExecutableNotFoundStrategyResult("docker");
        }

        context.dockerInspectorInfo = dockerInspectorManager.getDockerInspector(environment);
        if (context.dockerInspectorInfo == null) {
            return new InspectorNotFoundStrategyResult("docker");
        }

        return new PassedStrategyResult();
    }

}