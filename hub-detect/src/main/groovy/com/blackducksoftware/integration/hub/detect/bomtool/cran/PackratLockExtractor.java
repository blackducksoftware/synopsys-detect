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
package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

@Component
public class PackratLockExtractor {
    @Autowired
    private PackratPackager packratPackager;

    @Autowired
    private ExternalIdFactory externalIdFactory;

    @Autowired
    private DetectFileFinder detectFileFinder;

    public Extraction extract(final BomToolType bomToolType, final File directory, final File packratlock) {
        try {
            String projectName = "";
            String projectVersion = "";
            if (detectFileFinder.containsAllFiles(directory, "DESCRIPTION")) {
                final File descriptionFile = new File(directory, "DESCRIPTION");
                final List<String> descriptionText = Files.readAllLines(descriptionFile.toPath(), StandardCharsets.UTF_8);
                projectName = packratPackager.getProjectName(descriptionText);
                projectVersion = packratPackager.getVersion(descriptionText);
            }
            final List<String> packratLockText = Files.readAllLines(packratlock.toPath(), StandardCharsets.UTF_8);
            final DependencyGraph dependencyGraph = packratPackager.extractProjectDependencies(packratLockText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CRAN, directory.toString());
            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.CRAN, bomToolType, directory.toString(), externalId, dependencyGraph).build();
            return new Extraction.Builder().success(codeLocation).projectName(projectName).projectVersion(projectVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
