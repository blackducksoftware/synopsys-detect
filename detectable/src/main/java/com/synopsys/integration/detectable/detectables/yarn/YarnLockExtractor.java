/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListNode;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnListParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

public class YarnLockExtractor {
    public static final String OUTPUT_FILE = "detect_yarn_proj_dependencies.txt";
    public static final String ERROR_FILE = "detect_yarn_error.txt";

    private final ExternalIdFactory externalIdFactory;
    private final YarnListParser yarnListParser;
    private final YarnLockParser yarnLockParser;
    private final YarnLockOptions yarnLockOptions;
    private final ExecutableRunner executableRunner;
    private final YarnTransformer yarnTransformer;

    public YarnLockExtractor(final ExternalIdFactory externalIdFactory, final YarnListParser yarnListParser, final ExecutableRunner executableRunner,
        final YarnLockParser yarnLockParser, final YarnLockOptions yarnLockOptions, final YarnTransformer yarnTransformer) {
        this.externalIdFactory = externalIdFactory;
        this.yarnListParser = yarnListParser;
        this.yarnLockParser = yarnLockParser;
        this.executableRunner = executableRunner;
        this.yarnLockOptions = yarnLockOptions;
        this.yarnTransformer = yarnTransformer;
    }

    public Extraction extract(final File directory, final File yarnlock, final File yarnExe) {
        try {
            final List<String> yarnLockText = Files.readAllLines(yarnlock.toPath(), StandardCharsets.UTF_8);
            final List<String> exeArgs = Stream.of("list", "--emoji", "false").collect(Collectors.toCollection(ArrayList::new));

            if (yarnLockOptions.useProductionOnly()) {
                exeArgs.add("--prod");
            }

            final ExecutableOutput executableOutput = executableRunner.execute(directory, yarnExe, exeArgs);

            if (executableOutput.getReturnCode() != 0) {
                final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", exeArgs), executableOutput.getReturnCode()));
                return builder.build();
            }

            YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);
            List<YarnListNode> yarnList = yarnListParser.parseYarnList(executableOutput.getStandardOutputAsList());

            final DependencyGraph dependencyGraph = yarnTransformer.transform(yarnList, yarnLock);

            final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
