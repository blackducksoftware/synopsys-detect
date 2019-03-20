/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;

public class CondaCliExtractor {
    private final CondaListParser condaListParser;
    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final CondaCliDetectableOptions condaCliDetectableOptions;

    public CondaCliExtractor(final CondaListParser condaListParser, final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner, final CondaCliDetectableOptions condaCliDetectableOptions) {
        this.condaListParser = condaListParser;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.condaCliDetectableOptions = condaCliDetectableOptions;
    }

    public Extraction extract(final File directory, final File condaExe, final File workingDirectory) {
        try {
            final List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            final String condaEnvironmentName = condaCliDetectableOptions.getCondaEnvironmentName();
            if (StringUtils.isNotBlank(condaEnvironmentName)) {
                condaListOptions.add("-n");
                condaListOptions.add(condaEnvironmentName);
            }
            condaListOptions.add("--json");
            final ExecutableOutput condaListOutput = executableRunner.execute(directory, condaExe, condaListOptions);

            final String listJsonText = condaListOutput.getStandardOutput();

            final ExecutableOutput condaInfoOutput = executableRunner.execute(workingDirectory, condaExe, "info", "--json");
            final String infoJsonText = condaInfoOutput.getStandardOutput();

            final DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.ANACONDA, directory.toString());
            final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph, externalId);

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
