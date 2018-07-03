/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.enumeration.PolicySeverityType;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.util.ResourceUtil;

public class ConfigurationManager {
    public static final String NUGET = "nuget";
    public static final String GRADLE = "gradle";
    public static final String DOCKER = "docker";
    private final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private final String USER_HOME = System.getProperty("user.home");

    private final TildeInPathResolver tildeInPathResolver;
    private final DetectConfigWrapper detectConfigWrapper;

    private List<String> bomToolSearchDirectoryExclusions;

    // properties to be updated
    private String sourcePath;
    private String outputDirectoryPath;
    private String bdioOutputDirectoryPath;
    private String scanOutputDirectoryPath;
    private String policyCheckFailOnSeverities;
    private int hubSignatureScannerParallelProcessors;
    private boolean hubOfflineMode;
    private String dockerInspectorAirGapPath;
    private String gradleInspectorAirGapPath;
    private String nugetInspectorAirGapPath;
    // end properties to be updated

    public ConfigurationManager(final TildeInPathResolver tildeInPathResolver, final DetectConfigWrapper detectConfigWrapper) {
        this.tildeInPathResolver = tildeInPathResolver;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public void initialize(List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        resolveTildeInPaths();
        resolveTargetAndOutputDirectories();
        resolvePolicyProperties();
        resolveSignatureScannerProperties(detectOptions);
        resolveBomToolSearchProperties();
        resolveAirGapPaths();

        updateDetectProperties(detectOptions);
    }

    private void resolveTildeInPaths() throws DetectUserFriendlyException {
        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_RESOLVE_TILDE_IN_PATHS)) {
            try {
                tildeInPathResolver.resolveTildeInAllPathFields(USER_HOME, detectConfigWrapper);
            } catch (IllegalAccessException e) {
                throw new DetectUserFriendlyException(String.format("There was a problem resolving the tilde's in the paths. %s", e.getMessage()), e, ExitCodeType.FAILURE_CONFIGURATION);
            }
        }
    }

    private void resolveTargetAndOutputDirectories() throws DetectUserFriendlyException {
        String sourcePath = detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH);
        if (StringUtils.isBlank(sourcePath)) {
            sourcePath = System.getProperty("user.dir");
        }

        String outputDirectoryPath = detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH);
        String bdioOutputDirectoryPath = detectConfigWrapper.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH);
        String scanOutputDirectoryPath = detectConfigWrapper.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH);

        File sourceDirectory = new File(sourcePath);

        // make sure the path is absolute
        try {
            sourcePath = sourceDirectory.getCanonicalPath();
            outputDirectoryPath = createDirectoryPath(outputDirectoryPath, USER_HOME, "blackduck");
            bdioOutputDirectoryPath = createDirectoryPath(bdioOutputDirectoryPath, outputDirectoryPath, "bdio");
            scanOutputDirectoryPath = createDirectoryPath(scanOutputDirectoryPath, outputDirectoryPath, "scan");
        } catch (IOException e) {
            throw new DetectUserFriendlyException(String.format("There was a problem creating . %s", e.getMessage()), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
        ensureDirectoryExists(outputDirectoryPath, "The system property 'user.home' will be used by default, but the output directory must exist.");
        ensureDirectoryExists(bdioOutputDirectoryPath, "By default, the directory 'bdio' will be created in the outputDirectory, but the directory must exist.");
        ensureDirectoryExists(scanOutputDirectoryPath, "By default, the directory 'scan' will be created in the outputDirectory, but the directory must exist.");

        this.sourcePath = sourcePath;
        this.outputDirectoryPath = outputDirectoryPath;
        this.bdioOutputDirectoryPath = bdioOutputDirectoryPath;
        this.scanOutputDirectoryPath = scanOutputDirectoryPath;
    }

    private void resolvePolicyProperties() {
        String policyCheckFailOnSeverities = detectConfigWrapper.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES);
        final boolean atLeastOnePolicySeverity = StringUtils.isNotBlank(policyCheckFailOnSeverities);
        if (atLeastOnePolicySeverity) {
            boolean allSeverities = false;
            final String[] splitSeverities = policyCheckFailOnSeverities.split(",");
            for (final String severity : splitSeverities) {
                if (severity.equalsIgnoreCase("ALL")) {
                    allSeverities = true;
                    break;
                }
            }
            if (allSeverities) {
                final List<String> allPolicyTypes = Arrays.stream(PolicySeverityType.values()).filter(type -> type != PolicySeverityType.UNSPECIFIED).map(type -> type.toString()).collect(Collectors.toList());
                this.policyCheckFailOnSeverities = StringUtils.join(allPolicyTypes, ",");
            } else {
                this.policyCheckFailOnSeverities = StringUtils.join(splitSeverities, ",");
            }
        }
    }

    private void resolveSignatureScannerProperties(List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        int hubSignatureScannerParallelProcessors = detectConfigWrapper.getIntegerProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS);
        if (hubSignatureScannerParallelProcessors == -1) {
            hubSignatureScannerParallelProcessors = Runtime.getRuntime().availableProcessors();
        }
        this.hubSignatureScannerParallelProcessors = hubSignatureScannerParallelProcessors;

        if (StringUtils.isNotBlank(detectConfigWrapper.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL)) &&
                StringUtils.isNotBlank(detectConfigWrapper.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH))) {
            throw new DetectUserFriendlyException(
                    "You have provided both a hub signature scanner url AND a local hub signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                    ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        if (StringUtils.isNotBlank(detectConfigWrapper.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL))) {
            logger.info("A hub signature scanner url was provided, which requires hub offline mode. Setting hub offline mode to true.");
            if (hubOfflineMode == false) {
                addFieldWarning(detectOptions, DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL, "A hub signature scanner host url was provided but hub offline mode was false. In the future set hub offline mode to true.");
                addFieldWarning(detectOptions, DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE, "A signature scanner url was provided, so hub offline mode was forced to true.");
            }
            hubOfflineMode = true;
        }
        if (StringUtils.isNotBlank(detectConfigWrapper.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH))) {
            logger.info("A local hub signature scanner path was provided, which requires hub offline mode. Setting hub offline mode to true.");
            if (hubOfflineMode == false) {
                addFieldWarning(detectOptions, DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, "A local hub signature scanner was provided but hub offline mode was false. In the future set hub offline mode to true.");
                addFieldWarning(detectOptions, DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE, "A signature scanner path was provided, so hub offline mode was forced to true.");
            }
            hubOfflineMode = true;
        }
    }

    private void resolveBomToolSearchProperties() throws DetectUserFriendlyException {
        bomToolSearchDirectoryExclusions = new ArrayList<>();
        for (final String exclusion : detectConfigWrapper.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION)) {
            bomToolSearchDirectoryExclusions.add(exclusion);
        }
        try {
            if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS)) {
                final String fileContent = ResourceUtil.getResourceAsString(ConfigurationManager.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
                bomToolSearchDirectoryExclusions.addAll(Arrays.asList(fileContent.split("\r?\n")));
            }
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not determine the directories to exclude from the bom tool search. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private void resolveAirGapPaths() {
        dockerInspectorAirGapPath = getInspectorAirGapPath(detectConfigWrapper.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH), DOCKER);
        gradleInspectorAirGapPath = getInspectorAirGapPath(detectConfigWrapper.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH), GRADLE);
        nugetInspectorAirGapPath = getInspectorAirGapPath(detectConfigWrapper.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH), NUGET);
    }

    private void updateDetectProperties(List<DetectOption> detectOptions) {
        updateOptionValue(detectOptions, DetectProperty.DETECT_SOURCE_PATH, sourcePath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, sourcePath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_OUTPUT_PATH, outputDirectoryPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_OUTPUT_PATH, outputDirectoryPath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_BDIO_OUTPUT_PATH, bdioOutputDirectoryPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH, bdioOutputDirectoryPath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_SCAN_OUTPUT_PATH, scanOutputDirectoryPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH, scanOutputDirectoryPath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, policyCheckFailOnSeverities);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, policyCheckFailOnSeverities);

        updateOptionValue(detectOptions, DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, String.valueOf(hubSignatureScannerParallelProcessors));
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, String.valueOf(hubSignatureScannerParallelProcessors));

        updateOptionValue(detectOptions, DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE, String.valueOf(hubOfflineMode));
        detectConfigWrapper.setDetectProperty(DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE, String.valueOf(hubOfflineMode));

        updateOptionValue(detectOptions, DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, StringUtils.join(bomToolSearchDirectoryExclusions, ","));
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, StringUtils.join(bomToolSearchDirectoryExclusions, ","));

        updateOptionValue(detectOptions, DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH, dockerInspectorAirGapPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH, dockerInspectorAirGapPath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH, gradleInspectorAirGapPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH, gradleInspectorAirGapPath);

        updateOptionValue(detectOptions, DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH, nugetInspectorAirGapPath);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH, nugetInspectorAirGapPath);
    }

    private void updateOptionValue(List<DetectOption> detectOptions, final DetectProperty detectProperty, final String value) {
        detectOptions.stream().forEach(option -> {
            if (option.getDetectProperty() == detectProperty) {
                option.setPostInitValue(value);
            }
        });
    }

    private void addFieldWarning(List<DetectOption> detectOptions, final DetectProperty detectProperty, final String warning) {
        detectOptions.stream().forEach(option -> {
            if (option.getDetectProperty() == detectProperty) {
                option.addWarning(warning);
            }
        });
    }

    private void requestDeprecation(List<DetectOption> detectOptions, final DetectProperty detectProperty) {
        detectOptions.stream().forEach(option -> {
            if (option.getDetectProperty() == detectProperty) {
                option.requestDeprecation();
            }
        });
    }

    private String createDirectoryPath(final String providedDirectoryPath, final String defaultDirectoryPath, final String defaultDirectoryName) throws IOException {
        if (StringUtils.isBlank(providedDirectoryPath)) {
            final File directory = new File(defaultDirectoryPath, defaultDirectoryName);
            return directory.getCanonicalPath();
        }
        return providedDirectoryPath;
    }

    private void ensureDirectoryExists(final String directoryPath, final String failureMessage) throws DetectUserFriendlyException {
        final File directory = new File(directoryPath);
        directory.mkdirs();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new DetectUserFriendlyException(String.format("The directory %s does not exist. %s", directoryPath, failureMessage), ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private String guessDetectJarLocation() {
        final String containsDetectJarRegex = ".*hub-detect-[^\\\\/]+\\.jar.*";
        final String javaClasspath = System.getProperty("java.class.path");
        if (javaClasspath != null && javaClasspath.matches(containsDetectJarRegex)) {
            for (final String classpathChunk : javaClasspath.split(System.getProperty("path.separator"))) {
                if (classpathChunk != null && classpathChunk.matches(containsDetectJarRegex)) {
                    logger.debug(String.format("Guessed Detect jar location as %s", classpathChunk));
                    return classpathChunk;
                }
            }
        }
        return "";
    }

    private String getInspectorAirGapPath(final String inspectorLocationProperty, final String inspectorName) {
        if (StringUtils.isBlank(inspectorLocationProperty)) {
            try {
                final File detectJar = new File(guessDetectJarLocation()).getCanonicalFile();
                final File inspectorsDirectory = new File(detectJar.getParentFile(), "packaged-inspectors");
                final File inspectorAirGapDirectory = new File(inspectorsDirectory, inspectorName);
                return inspectorAirGapDirectory.getCanonicalPath();
            } catch (final Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }
}
