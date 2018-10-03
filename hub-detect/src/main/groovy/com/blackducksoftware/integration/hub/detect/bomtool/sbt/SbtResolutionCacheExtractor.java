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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class SbtResolutionCacheExtractor {
    private final Logger logger = LoggerFactory.getLogger(SbtResolutionCacheExtractor.class);

    private final DetectFileFinder detectFileFinder;
    private final ExternalIdFactory externalIdFactory;
    private final DetectConfiguration detectConfiguration;

    public SbtResolutionCacheExtractor(final DetectFileFinder detectFileFinder, final ExternalIdFactory externalIdFactory,
        final DetectConfiguration detectConfiguration) {
        this.detectFileFinder = detectFileFinder;
        this.externalIdFactory = externalIdFactory;
        this.detectConfiguration = detectConfiguration;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory) {
        try {
            final String included = detectConfiguration.getProperty(DetectProperty.DETECT_SBT_INCLUDED_CONFIGURATIONS);
            final String excluded = detectConfiguration.getProperty(DetectProperty.DETECT_SBT_EXCLUDED_CONFIGURATIONS);

            final int depth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_SBT_REPORT_DEPTH);

            final SbtPackager packager = new SbtPackager(externalIdFactory, detectFileFinder);
            final SbtProject project = packager.extractProject(directory.getAbsolutePath(), depth, included, excluded);

            final List<DetectCodeLocation> codeLocations = new ArrayList<>();

            String projectName = null;
            String projectVersion = null;
            for (final SbtDependencyModule module : project.modules) {
                final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.SBT, bomToolType, directory.toString(), project.projectExternalId, module.graph).build();
                if (projectName == null) {
                    projectName = project.projectName;
                    projectVersion = project.projectVersion;
                }
                codeLocations.add(codeLocation);
            }

            if (codeLocations.size() > 0) {
                return new Extraction.Builder().success(codeLocations).projectName(projectName).projectVersion(projectVersion).build();
            } else {
                logger.error("Unable to find any dependency information.");
                return new Extraction.Builder().failure("Unable to find any dependency information.").build();
            }

        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
