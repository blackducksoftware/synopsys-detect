/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipeline;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final WorkspaceRuleChooser workspaceRuleChooser;

    public BazelExtractor(final ExecutableRunner executableRunner,
        final ExternalIdFactory externalIdFactory,
        final WorkspaceRuleChooser workspaceRuleChooser) {
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
        this.workspaceRuleChooser = workspaceRuleChooser;
    }

    public Extraction extract(final File bazelExe, final File workspaceDir, final BazelWorkspace bazelWorkspace, final String bazelTarget,
            final BazelProjectNameGenerator bazelProjectNameGenerator, final WorkspaceRule providedBazelDependencyType,
            final List<String> providedCqueryAdditionalOptions) {
        logger.debug("Bazel extraction:");
        try {
            final WorkspaceRule ruleFromWorkspaceFile = bazelWorkspace.getDependencyRule();
            final BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
            final BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget, providedCqueryAdditionalOptions);
            final Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor, externalIdFactory);
            final WorkspaceRule workspaceRule = workspaceRuleChooser.choose(ruleFromWorkspaceFile, providedBazelDependencyType);
            final Pipeline pipeline = pipelines.get(workspaceRule);
            final MutableDependencyGraph dependencyGraph = pipeline.run();
            final CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            final List<CodeLocation> codeLocations = Arrays.asList(codeLocation);
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
