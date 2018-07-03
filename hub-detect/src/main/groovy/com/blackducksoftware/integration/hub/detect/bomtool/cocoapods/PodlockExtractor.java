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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.parse.PodlockParser;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class PodlockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PodlockParser podlockParser;
    private final ExternalIdFactory externalIdFactory;

    @Autowired
    public PodlockExtractor(final PodlockParser podlockParser, final ExternalIdFactory externalIdFactory) {
        this.podlockParser = podlockParser;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(final File directory, final File podlock) {
        String podLockText;
        try {
            logger.trace(String.format("Reading from the pod lock file %s", podlock.getAbsolutePath()));
            podLockText = FileUtils.readFileToString(podlock, StandardCharsets.UTF_8);
            logger.trace("Finished reading from the pod lock file.");
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        DependencyGraph dependencyGraph;
        try {
            logger.trace("Attempting to create the dependency graph from the pod lock file.");
            dependencyGraph = podlockParser.extractDependencyGraph(podLockText);
            logger.trace("Finished creating the dependency graph from the pod lock file.");
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.COCOAPODS, directory.toString());

        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.COCOAPODS, directory.toString(), externalId, dependencyGraph).build();
        
        return new Extraction.Builder().success(codeLocation).build();
    }

}
