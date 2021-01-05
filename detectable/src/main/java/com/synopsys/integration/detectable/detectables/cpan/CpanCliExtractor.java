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
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class CpanCliExtractor {
    private final CpanListParser cpanListParser;
    private final DetectableExecutableRunner executableRunner;

    public CpanCliExtractor(final CpanListParser cpanListParser, final DetectableExecutableRunner executableRunner) {
        this.cpanListParser = cpanListParser;
        this.executableRunner = executableRunner;
    }

    public Extraction extract(final File cpanExe, final File cpanmExe, final File workingDirectory) {
        try {
            final ExecutableOutput cpanListOutput = executableRunner.execute(workingDirectory, cpanExe, "-l");
            final List<String> listText = cpanListOutput.getStandardOutputAsList();

            final ExecutableOutput showdepsOutput = executableRunner.execute(workingDirectory, cpanmExe, "--showdeps", ".");
            final List<String> showdeps = showdepsOutput.getStandardOutputAsList();

            final DependencyGraph dependencyGraph = cpanListParser.parse(listText, showdeps);
            final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
