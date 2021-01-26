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
package com.synopsys.integration.detectable.detectables.gradle.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;

public class GradleParseExtractor {
    private final BuildGradleParser buildGradleParser;

    public GradleParseExtractor(final BuildGradleParser buildGradleParser) {
        this.buildGradleParser = buildGradleParser;
    }

    public Extraction extract(final File buildFile) {
        try (final InputStream buildFileInputStream = new FileInputStream(buildFile)) {
            final Optional<DependencyGraph> dependencyGraph = buildGradleParser.parse(buildFileInputStream);

            if (dependencyGraph.isPresent()) {
                final CodeLocation codeLocation = new CodeLocation(dependencyGraph.get());
                return new Extraction.Builder().success(codeLocation).build();
            } else {
                return new Extraction.Builder().failure("Failed to extract dependencies.").build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
