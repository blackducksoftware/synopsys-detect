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
package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;

public class SbtPluginExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final SbtPluginOutputParser sbtPluginOutputParser;

    public SbtPluginExtractor(DetectableExecutableRunner executableRunner, final SbtPluginOutputParser sbtPluginOutputParser) {
        this.executableRunner = executableRunner;
        this.sbtPluginOutputParser = sbtPluginOutputParser;
    }

    public Extraction extract(File directory, ExecutableTarget sbt, SbtPlugin plugin) {
        try {
            Executable executable = ExecutableUtils.createFromTarget(directory, sbt, plugin.getArguments());
            ExecutableOutput output = executableRunner.executeSuccessfully(executable);
            List<DependencyGraph> dependencyGraphs = sbtPluginOutputParser.parse(plugin.getLineParser(), output.getStandardOutputAsList());
            List<CodeLocation> codeLocations = dependencyGraphs.stream().map(CodeLocation::new).collect(Collectors.toList());
            return new Extraction.Builder().success(codeLocations).build();
        } catch (ExecutableFailedException e) {
            return Extraction.fromFailedExecutable(e);
        }
    }

}
