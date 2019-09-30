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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalId;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.PipelineJsonProcessor;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.Step;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.StepExecutorEdit;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.StepExecutorExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.StepExecutorParseEachXml;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.StepExecutorSplit;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelCodeLocationBuilder;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelQueryXmlOutputParser;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelVariableSubstitutor;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final BazelCodeLocationBuilder codeLocationGenerator;
    private final PipelineJsonProcessor pipelineJsonProcessor;

    public BazelExtractor(final ExecutableRunner executableRunner,
        final BazelCodeLocationBuilder codeLocationGenerator, final PipelineJsonProcessor pipelineJsonProcessor) {
        this.executableRunner = executableRunner;
        this.codeLocationGenerator = codeLocationGenerator;
        this.pipelineJsonProcessor = pipelineJsonProcessor;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(final File bazelExe, final File workspaceDir, String bazelTarget, String pipelineStepsPath) {
        logger.debug("Bazel extractAndPublishResults()");
        try {
            codeLocationGenerator.setWorkspaceDir(workspaceDir);
            List<Step> pipelineSteps;
            if (StringUtils.isNotBlank(pipelineStepsPath)) {
                pipelineSteps = loadPipelineStepsFromFile(pipelineStepsPath);
                logger.debug(String.format("Read %d pipeline step(s) from %s", pipelineSteps.size(), pipelineStepsPath));
            } else {
                // TODO figure out how to ship with a default, plus a few known pipelines
                throw new OperationNotSupportedException("Have not implemented built-in pipeline steps yet");
//                BazelExternalIdExtractionSimpleRules simpleRules = new BazelExternalIdExtractionSimpleRules(pipelineStepsPath);
//                pipelineSteps = simpleRules.getRules().stream()
//                                      .map(RuleConverter::simpleToFull).collect(Collectors.toList());
//                if (logger.isDebugEnabled()) {
//                    logger.debug(String.format("Using default rules:\n%s", pipelineJsonProcessor.toJson(pipelineSteps)));
//                }
            }
            final BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
            final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget);


            // TODO get step executor list programmatically
            final StepExecutor stepExecutorExecuteBazelOnEach = new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor);
            final StepExecutor stepExecutorSplitEach = new StepExecutorSplit();
            final StepExecutor stepExecutorEdit = new StepExecutorEdit();
            final StepExecutor stepExecutorParseEachXml = new StepExecutorParseEachXml();

            List<String> pipelineData = new ArrayList<>();
            for (final Step step : pipelineSteps) {
                // TODO get step executor list programmatically
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

    private List<Step> loadPipelineStepsFromFile(final String pipelineStepsJsonFilePath) throws IOException {
        final File jsonFile = new File(pipelineStepsJsonFilePath);
        List<Step> loadedSteps = pipelineJsonProcessor.load(jsonFile);
        return loadedSteps;
    }
}
