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
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final BazelCodeLocationBuilder codeLocationGenerator;
    private final WorkspaceRuleChooser workspaceRuleChooser;

    public BazelExtractor(final ExecutableRunner executableRunner,
        final BazelCodeLocationBuilder codeLocationGenerator, final WorkspaceRuleChooser workspaceRuleChooser) {
        this.executableRunner = executableRunner;
        this.codeLocationGenerator = codeLocationGenerator;
        this.workspaceRuleChooser = workspaceRuleChooser;
    }

    public Extraction extract(final File bazelExe, final File workspaceDir, final BazelWorkspace bazelWorkspace, final String bazelTarget,
        final BazelProjectNameGenerator bazelProjectNameGenerator, final String providedBazelDependencyType) {
        logger.debug("Bazel extraction:");
        try {
            final WorkspaceRule ruleFromWorkspaceFile = bazelWorkspace.getDependencyRule();
            final BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
            final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget);
            final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor);
            final WorkspaceRule workspaceRule = workspaceRuleChooser.choose(ruleFromWorkspaceFile, providedBazelDependencyType);
            final List<StepExecutor> pipeline = pipelines.get(workspaceRule);

            // Execute pipeline steps (like linux cmd piping with '|'); each step processes the output of the previous step
            List<String> pipelineData = new ArrayList<>();
            for (final StepExecutor pipelineStep : pipeline) {
                pipelineData = pipelineStep.process(pipelineData);
            }
            // final pipelineData is a list of group:artifact:version strings
            for (String artifactString : pipelineData) {
                final String[] gavParts = artifactString.split(":");
                codeLocationGenerator.addDependency(gavParts[0], gavParts[1], gavParts[2]);
            }
            final List<CodeLocation> codeLocations = codeLocationGenerator.build();
            final String projectName = bazelProjectNameGenerator.generateFromBazelTarget(bazelTarget);
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
}
