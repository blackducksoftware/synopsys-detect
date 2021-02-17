/*
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
package com.synopsys.integration.detectable.detectables.conda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class CondaCliExtractor {
    private final CondaListParser condaListParser;
    private final DetectableExecutableRunner executableRunner;

    public CondaCliExtractor(CondaListParser condaListParser, DetectableExecutableRunner executableRunner) {
        this.condaListParser = condaListParser;
        this.executableRunner = executableRunner;
    }

    public Extraction extract(File directory, ExecutableTarget condaExe, File workingDirectory, String condaEnvironmentName) {
        try {
            List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            if (StringUtils.isNotBlank(condaEnvironmentName)) {
                condaListOptions.add("-n");
                condaListOptions.add(condaEnvironmentName);
            }
            condaListOptions.add("--json");
            ExecutableOutput condaListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(directory, condaExe, condaListOptions));

            String listJsonText = condaListOutput.getStandardOutput();

            ExecutableOutput condaInfoOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, condaExe, "info", "--json"));
            String infoJsonText = condaInfoOutput.getStandardOutput();

            DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
