/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.conda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.parse.CondaListParser;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public class CondaCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(CondaCliExtractor.class);

    private final CondaListParser condaListParser;
    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final DetectConfigWrapper detectConfigWrapper;

    public CondaCliExtractor(final CondaListParser condaListParser, final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner, final DetectConfigWrapper detectConfigWrapper) {
        this.condaListParser = condaListParser;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public Extraction extract(final File directory, final File condaExe) {
        try {
            final List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            String condaEnvironmentName = detectConfigWrapper.getProperty(DetectProperty.DETECT_CONDA_ENVIRONMENT_NAME);
            if (StringUtils.isNotBlank(condaEnvironmentName)) {
                condaListOptions.add("-n");
                condaListOptions.add(condaEnvironmentName);
            }
            condaListOptions.add("--json");
            final Executable condaListExecutable = new Executable(directory, condaExe, condaListOptions);
            final ExecutableOutput condaListOutput = executableRunner.execute(condaListExecutable);

            final String listJsonText = condaListOutput.getStandardOutput();

            final ExecutableOutput condaInfoOutput = executableRunner.runExe(condaExe, "info", "--json");
            final String infoJsonText = condaInfoOutput.getStandardOutput();

            final DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.ANACONDA, directory.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.CONDA, directory.toString(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
