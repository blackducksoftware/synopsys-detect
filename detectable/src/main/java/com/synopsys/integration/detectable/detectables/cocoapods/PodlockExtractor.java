/*
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
package com.synopsys.integration.detectable.detectables.cocoapods;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;

public class PodlockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PodlockParser podlockParser;

    public PodlockExtractor(final PodlockParser podlockParser) {
        this.podlockParser = podlockParser;
    }

    public Extraction extract(final File podlock) {
        final String podLockText;
        try {
            logger.trace(String.format("Reading from the pod lock file %s", podlock.getAbsolutePath()));
            podLockText = FileUtils.readFileToString(podlock, StandardCharsets.UTF_8);
            logger.debug(podLockText);
            logger.trace("Finished reading from the pod lock file.");
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        final DependencyGraph dependencyGraph;
        try {
            logger.trace("Attempting to create the dependency graph from the pod lock file.");
            dependencyGraph = podlockParser.extractDependencyGraph(podLockText);
            logger.trace("Finished creating the dependency graph from the pod lock file.");
        } catch (final IOException | MissingExternalIdException e) {
            return new Extraction.Builder().exception(e).build();
        }

        final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        return new Extraction.Builder().success(codeLocation).build();
    }

}
