/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.exception.IntegrationException;

public class BazelPipelineLoader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BazelPipelineJsonProcessor bazelPipelineJsonProcessor;

    public BazelPipelineLoader(final BazelPipelineJsonProcessor bazelPipelineJsonProcessor) {
        this.bazelPipelineJsonProcessor = bazelPipelineJsonProcessor;
    }

    @NotNull
    public List<Step> loadPipelineSteps(final ClasspathFileReader classpathFileReader, final String ruleFromWorkspaceFile, final String providedBazelDependencyType) throws IntegrationException {
        final String finalBazelDependencyType;
        if (StringUtils.isNotBlank(providedBazelDependencyType) && !"UNSPECIFIED".equalsIgnoreCase(providedBazelDependencyType)) {
            finalBazelDependencyType = providedBazelDependencyType;
        } else if (ruleFromWorkspaceFile != null) {
            finalBazelDependencyType = ruleFromWorkspaceFile;
        } else {
            throw new IntegrationException("Unable to determine Workspace dependency rule; try setting it via the property");
        }
        final List<Step> pipelineSteps = loadPipelineStepsForTypeFromClasspath(classpathFileReader, finalBazelDependencyType);
        if (pipelineSteps == null) {
            throw new IntegrationException(String.format("Workspace dependency type %s is not supported", finalBazelDependencyType));
        }
        return pipelineSteps;
    }

    private List<Step> loadPipelineStepsForTypeFromClasspath(final ClasspathFileReader classpathFileReader, final String bazelDependencyType) throws IntegrationException {
        final String pipelineStepsJsonLoadPath = derivePipelineStepsLoadPath(bazelDependencyType);
        final List<Step> loadedSteps = loadPipelineStepsAtPathFromClasspath(classpathFileReader, pipelineStepsJsonLoadPath);
        return loadedSteps;
    }

    private String derivePipelineStepsLoadPath(final String bazelDependencyType) {
        logger.debug(String.format("Loading pipeline steps for %s", bazelDependencyType));
        final String pipelineStepsJsonFilePath;
        if (bazelDependencyType.startsWith("file:")) {
            // for developer use only
            pipelineStepsJsonFilePath = bazelDependencyType;
        } else {
            pipelineStepsJsonFilePath = String.format("/bazel/pipeline/%s.json", bazelDependencyType);
        }
        return pipelineStepsJsonFilePath;
    }

    @NotNull
    private List<Step> loadPipelineStepsAtPathFromClasspath(final ClasspathFileReader classpathFileReader, final String pipelineStepsJsonFilePath) throws IntegrationException {
        final String jsonString;
        try {
            jsonString = classpathFileReader.readFileFromClasspathToString(pipelineStepsJsonFilePath);
        } catch (IOException e) {
            throw new IntegrationException("Unable to read pipeline steps", e);
        }
        return bazelPipelineJsonProcessor.fromJsonString(jsonString);
    }
}
