/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.conda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.util.executable.Executable;
import com.synopsys.integration.detect.util.executable.ExecutableOutput;
import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationType;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CondaCliExtractor {
    private final CondaListParser condaListParser;
    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;

    public CondaCliExtractor(final CondaListParser condaListParser, final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration, DirectoryManager directoryManager) {
        this.condaListParser = condaListParser;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
    }

    public Extraction extract(final File directory, final File condaExe, ExtractionId extractionId) {
        try {
            File workingDirectory = directoryManager.getExtractionOutputDirectory(extractionId);

            final List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            final String condaEnvironmentName = detectConfiguration.getProperty(DetectProperty.DETECT_CONDA_ENVIRONMENT_NAME, PropertyAuthority.None);
            if (StringUtils.isNotBlank(condaEnvironmentName)) {
                condaListOptions.add("-n");
                condaListOptions.add(condaEnvironmentName);
            }
            condaListOptions.add("--json");
            final Executable condaListExecutable = new Executable(directory, condaExe, condaListOptions);
            final ExecutableOutput condaListOutput = executableRunner.execute(condaListExecutable);

            final String listJsonText = condaListOutput.getStandardOutput();

            final ExecutableOutput condaInfoOutput = executableRunner.execute(workingDirectory, condaExe, "info", "--json");
            final String infoJsonText = condaInfoOutput.getStandardOutput();

            final DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.ANACONDA, directory.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.CONDA, directory.toString(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
