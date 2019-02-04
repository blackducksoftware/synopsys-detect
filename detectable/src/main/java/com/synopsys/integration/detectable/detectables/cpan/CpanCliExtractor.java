/**
 * hub-detect
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
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;

public class CpanCliExtractor {
    private final CpanListParser cpanListParser;
    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;

    public CpanCliExtractor(final CpanListParser cpanListParser, final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner) {
        this.cpanListParser = cpanListParser;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
    }

    public Extraction extract(final File directory, final File cpanExe, final File cpanmExe, final File workingDirectory) {
        try {
            final ExecutableOutput cpanListOutput = executableRunner.execute(workingDirectory, cpanExe, "-l");
            final List<String> listText = cpanListOutput.getStandardOutputAsList();

            final ExecutableOutput showdepsOutput = executableRunner.execute(workingDirectory, cpanmExe, "--showdeps", ".");
            final List<String> showdeps = showdepsOutput.getStandardOutputAsList();

            final DependencyGraph dependencyGraph = cpanListParser.parse(listText, showdeps);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CPAN, directory.toString());
            final CodeLocation detectCodeLocation = new CodeLocation.Builder(CodeLocationType.CPAN, dependencyGraph, externalId).build();
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
