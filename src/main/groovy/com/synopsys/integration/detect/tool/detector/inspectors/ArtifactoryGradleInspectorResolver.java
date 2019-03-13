/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.file.AirGapManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.exception.IntegrationException;

import freemarker.template.Configuration;

public class ArtifactoryGradleInspectorResolver implements GradleInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GRADLE_DIR_NAME = "gradle";
    private static final String GENERATED_GRADLE_SCRIPT_NAME = "init-detect.gradle";

    private final ArtifactResolver artifactResolver;
    private final Configuration configuration;
    private final GradleInspectorScriptOptions gradleInspectorScriptOptions;
    private final AirGapManager airGapManager;
    private final DirectoryManager directoryManager;

    private File generatedGradleScriptPath = null;
    private boolean hasResolvedInspector = false;

    public ArtifactoryGradleInspectorResolver(final ArtifactResolver artifactResolver, final Configuration configuration, final GradleInspectorScriptOptions gradleInspectorScriptOptions, final AirGapManager airGapManager,
        final DirectoryManager directoryManager) {
        this.artifactResolver = artifactResolver;
        this.configuration = configuration;
        this.gradleInspectorScriptOptions = gradleInspectorScriptOptions;
        this.airGapManager = airGapManager;
        this.directoryManager = directoryManager;
    }

    @Override
    public File resolveGradleInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            try {
                final Optional<File> airGapPath = airGapManager.getGradleInspectorAirGapFile();
                final File generatedGradleScriptFile = directoryManager.getSharedFile(GRADLE_DIR_NAME, GENERATED_GRADLE_SCRIPT_NAME);
                final GradleInspectorScriptCreator gradleInspectorScriptCreator = new GradleInspectorScriptCreator(configuration);
                if (airGapPath.isPresent()) {
                    generatedGradleScriptPath = gradleInspectorScriptCreator.createOfflineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions, airGapPath.get().getCanonicalPath());
                } else {
                    final Optional<String> version = gradleInspectorScriptOptions.getProvidedOnlineInspectorVersion() //TODO: i don't like this because it looks like stateless stream ops but is not, we call a method that makes web requests... - jp
                                                         .map(this::findVersion)
                                                         .filter(Optional::isPresent)
                                                         .map(Optional::get);
                    if (version.isPresent()) {
                        logger.info("Resolved the gradle inspector version: " + version.get());
                        generatedGradleScriptPath = gradleInspectorScriptCreator.createOnlineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions, version.get());
                    } else {
                        throw new DetectableException("Unable to find the gradle inspector version from artifactory.");
                    }
                }
            } catch (final Exception e) {
                throw new DetectableException(e);
            }
            if (generatedGradleScriptPath == null) {
                throw new DetectableException("Unable to initialize the gradle inspector.");
            } else {
                logger.trace("Derived generated gradle script path: " + generatedGradleScriptPath);
            }
        } else {
            logger.debug("Already attempted to resolve the gradle inspector script, will not attempt again.");
        }
        if (generatedGradleScriptPath == null) {
            throw new DetectableException("Unable to find or create the gradle inspector script.");
        }

        return generatedGradleScriptPath;
    }



    private Optional<String> findVersion(final String suppliedGradleInspectorVersion) {
        try {
            return artifactResolver.resolveArtifactVersion(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.GRADLE_INSPECTOR_REPO, ArtifactoryConstants.GRADLE_INSPECTOR_PROPERTY, suppliedGradleInspectorVersion);
        } catch (final IntegrationException | IOException | DetectUserFriendlyException e) {
            logger.debug("Failed to fetch Gradle inspector version from Artifactory", e);
        }

        return Optional.empty();
    }

}
