/**
 * synopsys-detect
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
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.detectable.result.WrongOperatingSystemResult;
import com.synopsys.integration.util.OperatingSystemType;

public class DockerDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableEnvironment environment;
    private final DockerInspectorResolver dockerInspectorResolver;
    private final JavaResolver javaResolver;
    private final BashResolver bashResolver;
    private final DockerResolver dockerResolver;
    private final DockerExtractor dockerExtractor;
    private final DockerDetectableOptions dockerDetectableOptions;

    private File javaExe;
    private File bashExe;
    private DockerInspectorInfo dockerInspectorInfo;

    public DockerDetectable(final DetectableEnvironment environment, final DockerInspectorResolver dockerInspectorResolver, final JavaResolver javaResolver, final BashResolver bashResolver, final DockerResolver dockerResolver,
        final DockerExtractor dockerExtractor, final DockerDetectableOptions dockerDetectableOptions) {
        super(environment, "Docker", "DOCKER");
        this.environment = environment;
        this.javaResolver = javaResolver;
        this.bashResolver = bashResolver;
        this.dockerResolver = dockerResolver;
        this.dockerExtractor = dockerExtractor;
        this.dockerInspectorResolver = dockerInspectorResolver;
        this.dockerDetectableOptions = dockerDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (OperatingSystemType.determineFromSystem() == OperatingSystemType.WINDOWS) {
            return new WrongOperatingSystemResult(OperatingSystemType.determineFromSystem());
        }

        if (!dockerDetectableOptions.hasDockerImageOrTag()) {
            return new PropertyInsufficientDetectableResult();
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        javaExe = javaResolver.resolveJava();
        if (javaExe == null) {
            return new ExecutableNotFoundDetectableResult("java");
        }
        bashExe = bashResolver.resolveBash();
        if (bashExe == null) {
            return new ExecutableNotFoundDetectableResult("bash");
        }
        File dockerExe;
        try {
            dockerExe = dockerResolver.resolveDocker();
        } catch (final Exception e) {
            dockerExe = null;
        }
        if (dockerExe == null) {
            if (dockerDetectableOptions.isDockerPathRequired()) {
                return new ExecutableNotFoundDetectableResult("docker");
            } else {
                logger.info("Docker executable not found, but it has been configured as not-required; proceeding with execution of Docker tool");
            }
        }
        dockerInspectorInfo = dockerInspectorResolver.resolveDockerInspector();
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundDetectableResult("docker");
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        final String image = dockerDetectableOptions.getSuppliedDockerImage();
        final String tar = dockerDetectableOptions.getSuppliedDockerTar();
        return dockerExtractor.extract(environment.getDirectory(), extractionEnvironment.getOutputDirectory(), bashExe, javaExe, image, tar, dockerInspectorInfo);
    }
}