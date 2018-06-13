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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse.YarnListParser;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class YarnLockExtractor extends Extractor<YarnLockContext> {
    private final Logger logger = LoggerFactory.getLogger(YarnLockExtractor.class);
    public static final String OUTPUT_FILE = "detect_yarn_proj_dependencies.txt";
    public static final String ERROR_FILE = "detect_yarn_error.txt";

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Autowired
    DetectConfiguration detectConfiguration;

    @Autowired
    YarnListParser yarnListParser;

    @Autowired
    DetectFileManager detectFileManager;

    @Autowired
    ExecutableRunner executableRunner;

    @Override
    public Extraction extract(final YarnLockContext context) {
        try {
            final List<String> yarnLockText = Files.readAllLines(context.yarnlock.toPath(), StandardCharsets.UTF_8);
            final List<String> exeArgs = Arrays.asList("list", "--emoji", "false");

            if (detectConfiguration.getYarnProductionDependenciesOnly()) {
                exeArgs.add("--prod");
            }

            final Executable yarnListExe = new Executable(context.directory, context.yarnExe, exeArgs);
            final ExecutableOutput executableOutput = executableRunner.execute(yarnListExe);

            if (executableOutput.getReturnCode() != 0) {
                final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", exeArgs), executableOutput.getReturnCode()));
                return builder.build();
            }

            final DependencyGraph dependencyGraph = yarnListParser.parseYarnList(yarnLockText, executableOutput.getStandardOutputAsList());

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, context.directory.getCanonicalPath());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.YARN, context.directory.getCanonicalPath(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
