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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorEdit;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorFilter;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorParseEachXml;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorSplitEach;
import com.synopsys.integration.exception.IntegrationException;

public class Pipelines {
    private final Map<WorkspaceRule, List<StepExecutor>> availablePipelines = new HashMap<>();

    public Pipelines(final BazelCommandExecutor bazelCommandExecutor, final BazelVariableSubstitutor bazelVariableSubstitutor) {
        final List<StepExecutor> mavenJarPipeline = new ArrayList<>();
        mavenJarPipeline.add(new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("query", "filter('@.*:jar', deps(${detect.bazel.target}))")));
        mavenJarPipeline.add(new StepExecutorSplitEach("\\s+"));
        mavenJarPipeline.add(new StepExecutorEdit("^@", ""));
        mavenJarPipeline.add(new StepExecutorEdit("//.*", ""));
        mavenJarPipeline.add(new StepExecutorEdit("^", "//external:"));
        mavenJarPipeline.add(new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("query", "kind(maven_jar, ${0})", "--output", "xml")));
        mavenJarPipeline.add(new StepExecutorParseEachXml("/query/rule[@class='maven_jar']/string[@name='artifact']", "value"));
        availablePipelines.put(WorkspaceRule.MAVEN_JAR, mavenJarPipeline);

        final List<StepExecutor> mavenInstallPipeline = new ArrayList<>();
        mavenInstallPipeline.add(new StepExecutorExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(${detect.bazel.target}))", "--output", "build")));
        mavenInstallPipeline.add(new StepExecutorSplitEach("\n"));
        mavenInstallPipeline.add(new StepExecutorFilter(".*maven_coordinates=.*"));
        mavenInstallPipeline.add(new StepExecutorEdit("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", ""));
        mavenInstallPipeline.add(new StepExecutorEdit("\".*", ""));
        availablePipelines.put(WorkspaceRule.MAVEN_INSTALL, mavenInstallPipeline);
    }

    public List<StepExecutor> get(final WorkspaceRule bazelDependencyType) throws IntegrationException {
        if (!availablePipelines.containsKey(bazelDependencyType)) {
            throw new IntegrationException(String.format("No pipeline found for dependency type %s", bazelDependencyType.getName()));
        }
        return availablePipelines.get(bazelDependencyType);
    }
}
