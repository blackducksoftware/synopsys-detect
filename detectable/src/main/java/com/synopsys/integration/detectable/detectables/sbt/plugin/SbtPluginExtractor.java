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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class SbtPluginExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String PLUGIN_NAME = "net.virtualvoid.sbt.graph.DependencyGraphPlugin";

    private final DetectableExecutableRunner executableRunner;
    private final SbtPluginParser pluginParser;

    public SbtPluginExtractor(DetectableExecutableRunner executableRunner, final SbtPluginParser pluginParser) {
        this.executableRunner = executableRunner;
        this.pluginParser = pluginParser;
    }

    public Extraction extract(File directory, ExecutableTarget sbt) {
        Extraction happyPathExtraction = runPlugin(directory, sbt);
        if (happyPathExtraction == null || !happyPathExtraction.isSuccess()) {
            try {
                if (isPluginInstalled(directory, sbt)) {
                    return happyPathExtraction;
                } else {
                    return new Extraction.Builder().failure("Sbt requires the SBT plugin 'sbt-dependency-graph' be installed in the project or globally. Install the plugin to continue.").build();
                }
            } catch (ExecutableFailedException e) {
                logger.error("An issue occurred verifying the sbt dependency tree plugin was installed. Ensure the project builds and the plugin 'sbt-dependency-graph' is installed in the project or globally.");
                return Extraction.fromFailedExecutable(e);
            }
        }
        return happyPathExtraction;
    }

    public Extraction runPlugin(File directory, ExecutableTarget sbt) {
        try {
            ExecutableOutput output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, sbt, "dependencyTree"));
            List<CodeLocation> codeLocations = pluginParser.parse(output.getStandardOutputAsList()).stream().map(CodeLocation::new).collect(Collectors.toList());
            return new Extraction.Builder().success(codeLocations).build();
        } catch (ExecutableFailedException e) {
            return Extraction.fromFailedExecutable(e);
        }
    }

    public boolean isPluginInstalled(File directory, ExecutableTarget sbt) throws ExecutableFailedException {
        ExecutableOutput output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, sbt, "plugins"));
        return output.getStandardOutputAsList().stream().anyMatch(line -> line.contains(PLUGIN_NAME));
    }
}
