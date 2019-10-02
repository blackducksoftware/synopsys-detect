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
package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalId;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.BazelPipelineLoader;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorEdit;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorFilter;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorParseEachXml;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.StepExecutorSplitEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.BazelVariableSubstitutor;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final BazelCodeLocationBuilder codeLocationGenerator;
    private final BazelPipelineLoader bazelPipelineLoader;

    public BazelExtractor(final ExecutableRunner executableRunner,
        final BazelCodeLocationBuilder codeLocationGenerator, final BazelPipelineLoader bazelPipelineLoader) {
        this.executableRunner = executableRunner;
        this.codeLocationGenerator = codeLocationGenerator;
        this.bazelPipelineLoader = bazelPipelineLoader;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(final File bazelExe, final File workspaceDir, final WorkspaceRules workspaceRules, final String bazelTarget, final String bazelDependencyType) {
        logger.debug("Bazel extraction:");
        try {
            List<Step> pipelineSteps = bazelPipelineLoader.loadPipelineSteps(workspaceRules, bazelDependencyType);
            final BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
            final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget);

            // TODO get step executor list programmatically
            final StepExecutor stepExecutorExecuteBazelOnEach = new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor);
            final StepExecutor stepExecutorSplitEach = new StepExecutorSplitEach();
            final StepExecutor stepExecutorFilter = new StepExecutorFilter();
            final StepExecutor stepExecutorEdit = new StepExecutorEdit();
            final StepExecutor stepExecutorParseEachXml = new StepExecutorParseEachXml();

            List<String> pipelineData = new ArrayList<>();
            for (final Step step : pipelineSteps) {
                // TODO get step executor list programmatically
                logger.debug(String.format("Executing %s", step.getType()));
                if (stepExecutorExecuteBazelOnEach.applies(step.getType())) {
                    pipelineData = stepExecutorExecuteBazelOnEach.process(step, pipelineData);
                }
                if (stepExecutorSplitEach.applies(step.getType())) {
                    pipelineData = stepExecutorSplitEach.process(step, pipelineData);
                }
                if (stepExecutorEdit.applies(step.getType())) {
                    pipelineData = stepExecutorEdit.process(step, pipelineData);
                }
                if (stepExecutorParseEachXml.applies(step.getType())) {
                    pipelineData = stepExecutorParseEachXml.process(step, pipelineData);
                }
                if (stepExecutorFilter.applies(step.getType())) {
                    pipelineData = stepExecutorFilter.process(step, pipelineData);
                }
            }
            for (String artifactString : pipelineData) {
                BazelExternalId externalId = BazelExternalId.fromBazelArtifactString(artifactString, ":");
                logger.info(String.format("Adding externalId: %s", externalId));
                codeLocationGenerator.addDependency(externalId);
            }
            final List<CodeLocation> codeLocations = codeLocationGenerator.build();
            final String projectName = cleanProjectName(bazelTarget);
            final Extraction.Builder builder = new Extraction.Builder()
                                                   .success(codeLocations)
                                                   .projectName(projectName);
            return builder.build();
        } catch (Exception e) {
            final String msg = String.format("Bazel processing exception: %s", e.getMessage());
            logger.debug(msg, e);
            return new Extraction.Builder().failure(msg).build();
        }
    }

    private String cleanProjectName(final String bazelTarget) {
        String projectName = bazelTarget
                                 .replaceAll("^//", "")
                                 .replaceAll("^:", "")
                                 .replaceAll("/", "_")
                                 .replaceAll(":", "_");
        return projectName;
    }
}
