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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class YarnLockExtractor {
    public static final String OUTPUT_FILE = "detect_yarn_proj_dependencies.txt";
    public static final String ERROR_FILE = "detect_yarn_error.txt";

    private final ExternalIdFactory externalIdFactory;
    private final YarnListParser yarnListParser;
    private final ExecutableRunner executableRunner;
    private final boolean yarnProdOnly;

    public YarnLockExtractor(final ExternalIdFactory externalIdFactory, final YarnListParser yarnListParser, final ExecutableRunner executableRunner,
            final boolean yarnProdOnly) {
        this.externalIdFactory = externalIdFactory;
        this.yarnListParser = yarnListParser;
        this.executableRunner = executableRunner;
        this.yarnProdOnly = yarnProdOnly;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final File yarnlock, final String yarnExe) {
        try {
            final List<String> yarnLockText = Files.readAllLines(yarnlock.toPath(), StandardCharsets.UTF_8);
            final List<String> exeArgs = Stream.of("list", "--emoji", "false").collect(Collectors.toCollection(ArrayList::new));

            if (yarnProdOnly) {
                exeArgs.add("--prod");
            }

            final Executable yarnListExe = new Executable(directory, yarnExe, exeArgs);
            final ExecutableOutput executableOutput = executableRunner.execute(yarnListExe);

            if (executableOutput.getReturnCode() != 0) {
                final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", exeArgs), executableOutput.getReturnCode()));
                return builder.build();
            }

            final DependencyGraph dependencyGraph = yarnListParser.parseYarnList(yarnLockText, executableOutput.getStandardOutputAsList());

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, directory.getCanonicalPath());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.YARN, bomToolType, directory.getCanonicalPath(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
