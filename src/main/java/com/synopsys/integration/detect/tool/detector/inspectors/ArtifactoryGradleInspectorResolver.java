/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;

import freemarker.template.Configuration;

public class ArtifactoryGradleInspectorResolver implements GradleInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GRADLE_DIR_NAME = "gradle";
    private static final String GENERATED_GRADLE_SCRIPT_NAME = "init-detect.gradle";

    private final GradleInspectorInstaller gradleInspectorInstaller;
    private final Configuration configuration;
    private final GradleInspectorScriptOptions gradleInspectorScriptOptions;
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final DirectoryManager directoryManager;

    private File generatedGradleScriptPath = null;
    private boolean hasResolvedInspector = false;

    public ArtifactoryGradleInspectorResolver(final GradleInspectorInstaller gradleInspectorInstaller, final Configuration configuration, final GradleInspectorScriptOptions gradleInspectorScriptOptions,
        final AirGapInspectorPaths airGapInspectorPaths,
        final DirectoryManager directoryManager) {
        this.gradleInspectorInstaller = gradleInspectorInstaller;
        this.configuration = configuration;
        this.gradleInspectorScriptOptions = gradleInspectorScriptOptions;
        this.airGapInspectorPaths = airGapInspectorPaths;
        this.directoryManager = directoryManager;
    }

    @Override
    public File resolveGradleInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            try {
                final Optional<File> airGapPath = airGapInspectorPaths.getGradleInspectorAirGapFile();
                final File generatedGradleScriptFile = directoryManager.getSharedFile(GRADLE_DIR_NAME, GENERATED_GRADLE_SCRIPT_NAME);
                final GradleInspectorScriptCreator gradleInspectorScriptCreator = new GradleInspectorScriptCreator(configuration);
                if (airGapPath.isPresent()) {
                    generatedGradleScriptPath = gradleInspectorScriptCreator.createOfflineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions, airGapPath.get().getCanonicalPath());
                } else {
                    final String gradleInspectorVersion;
                    final Optional<String> providedOnlineInspectorVersion = gradleInspectorScriptOptions.getProvidedOnlineInspectorVersion();
                    if (providedOnlineInspectorVersion.isPresent()) {
                        logger.debug("Attempting to use the provided gradle inspector version.");
                        gradleInspectorVersion = providedOnlineInspectorVersion.get();
                    } else {
                        logger.debug("Attempting to resolve the gradle inspector version from artifactory.");
                        gradleInspectorVersion = gradleInspectorInstaller.findVersion();
                    }
                    logger.debug(String.format("Resolved the gradle inspector version: %s", gradleInspectorVersion));
                    generatedGradleScriptPath = gradleInspectorScriptCreator.createOnlineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions, gradleInspectorVersion);
                }
            } catch (final Exception e) {
                throw new DetectableException(e);
            }

            if (generatedGradleScriptPath == null) {
                throw new DetectableException("Unable to initialize the gradle inspector.");
            } else {
                logger.trace(String.format("Derived generated gradle script path: %s", generatedGradleScriptPath));
            }
        } else {
            logger.debug("Already attempted to resolve the gradle inspector script, will not attempt again.");
        }
        if (generatedGradleScriptPath == null) {
            throw new DetectableException("Unable to find or create the gradle inspector script.");
        }

        return generatedGradleScriptPath;
    }
}
