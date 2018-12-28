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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final BazelQueryXmlOutputParser parser;
    private final BazelExternalIdExtractionSimpleRules simpleRules;
    private final BazelBdioBuilder bdioGenerator;

    public BazelExtractor(final ExecutableRunner executableRunner, BazelQueryXmlOutputParser parser, final BazelExternalIdExtractionSimpleRules simpleRules, final BazelBdioBuilder bdioGenerator) {
        this.executableRunner = executableRunner;
        this.parser = parser;
        this.simpleRules = simpleRules;
        this.bdioGenerator = bdioGenerator;
    }

    public Extraction extract(final File workspaceDir, final int depth, final ExtractionId extractionId) {
        logger.debug("Bazel extract()");
        // TODO Should write and use BazelExecutableFinder like Gradle and MavenExecutableFinder
        try {
            bdioGenerator.setWorkspaceDir(workspaceDir);
            BazelExternalIdGenerator generator = new BazelExternalIdGenerator(executableRunner, parser, workspaceDir);
            simpleRules.getRules().stream()
                .map(BazelExternalIdExtractionXPathRule::new)
                .map(r -> generator.generate(r))
                .flatMap(Collection::stream)
                .forEach(i -> bdioGenerator.addDependency(i));
            final List<DetectCodeLocation> codeLocations = bdioGenerator.build();
            final Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
            return builder.build();
        } catch (Exception e) {
            final String msg = String.format("Bazel query threw exception: %s", e.getMessage());
            logger.error(msg, e);
            return new Extraction.Builder().failure(msg).build();
        }
    }
}
