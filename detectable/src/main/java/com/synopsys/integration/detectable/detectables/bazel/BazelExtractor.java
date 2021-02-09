/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipeline;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final WorkspaceRuleChooser workspaceRuleChooser;

    public BazelExtractor(DetectableExecutableRunner executableRunner,
        ExternalIdFactory externalIdFactory,
        WorkspaceRuleChooser workspaceRuleChooser) {
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
        this.workspaceRuleChooser = workspaceRuleChooser;
    }

    public Extraction extract(ExecutableTarget bazelExe, File workspaceDir, BazelWorkspace bazelWorkspace, String bazelTarget,
        BazelProjectNameGenerator bazelProjectNameGenerator, Set<WorkspaceRule> providedDependencyRuleTypes,
        List<String> providedCqueryAdditionalOptions) {
        logger.debug("Bazel extraction:");
        try {
            BazelCommandExecutor bazelCommandExecutor = new BazelCommandExecutor(executableRunner, workspaceDir, bazelExe);
            BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget, providedCqueryAdditionalOptions);
            Pipelines pipelines = new Pipelines(bazelCommandExecutor, bazelVariableSubstitutor, externalIdFactory);
            Set<WorkspaceRule> workspaceRulesToQuery = workspaceRuleChooser.choose(bazelWorkspace.getDependencyRuleTypes(), providedDependencyRuleTypes);
            List<Dependency> aggregatedDependencies = collectDependencies(pipelines, workspaceRulesToQuery);
            return buildResults(aggregatedDependencies, bazelProjectNameGenerator.generateFromBazelTarget(bazelTarget));
        } catch (Exception e) {
            String msg = String.format("Bazel processing exception: %s", e.getMessage());
            logger.debug(msg, e);
            return new Extraction.Builder().failure(msg).build();
        }
    }

    private Extraction buildResults(List<Dependency> aggregatedDependencies, String projectName) {
        MutableDependencyGraph dependencyGraph = createDependencyGraph(aggregatedDependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        List<CodeLocation> codeLocations = Collections.singletonList(codeLocation);
        Extraction.Builder builder = new Extraction.Builder()
                                         .success(codeLocations)
                                         .projectName(projectName);
        return builder.build();
    }

    @NotNull
    private List<Dependency> collectDependencies(Pipelines pipelines, Set<WorkspaceRule> workspaceRules) throws IntegrationException {
        List<Dependency> aggregatedDependencies = new ArrayList<>();
        // Make sure the order of processing deterministic
        List<WorkspaceRule> sortedWorkspaceRules = workspaceRules.stream()
                                                       .sorted(Comparator.naturalOrder())
                                                       .collect(Collectors.toList());

        for (WorkspaceRule workspaceRule : sortedWorkspaceRules) {
            logger.info(String.format("Running processing pipeline for rule %s", workspaceRule));
            Pipeline pipeline = pipelines.get(workspaceRule);
            List<Dependency> ruleDependencies = pipeline.run();
            logger.info(String.format("Number of dependencies discovered for rule %s: %d", workspaceRule, ruleDependencies.size()));
            logger.debug(String.format("Dependencies discovered for rule %s: %s", workspaceRule, ruleDependencies));
            aggregatedDependencies.addAll(ruleDependencies);
        }
        return aggregatedDependencies;
    }

    @NotNull
    private MutableDependencyGraph createDependencyGraph(List<Dependency> aggregatedDependencies) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (Dependency dependency : aggregatedDependencies) {
            dependencyGraph.addChildToRoot(dependency);
        }
        return dependencyGraph;
    }
}
