/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.property.PropertyType;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;

public class DetectConfigurationManager {
    public final static String USER_HOME = System.getProperty("user.home");
    private final Logger logger = LoggerFactory.getLogger(DetectConfigurationManager.class);

    private final TildeInPathResolver tildeInPathResolver;
    private final DetectConfiguration detectConfiguration;

    private List<String> bomToolSearchDirectoryExclusions;

    // properties to be updated
    private String policyCheckFailOnSeverities;
    private int hubSignatureScannerParallelProcessors;
    private boolean hubOfflineMode;
    // end properties to be updated

    public DetectConfigurationManager(final TildeInPathResolver tildeInPathResolver, final DetectConfiguration detectConfiguration) {
        this.tildeInPathResolver = tildeInPathResolver;
        this.detectConfiguration = detectConfiguration;
    }

    public void process(final List<DetectOption> detectOptions, String runId) throws DetectUserFriendlyException {
        resolveTildeInPaths();
        resolvePolicyProperties();
        resolveSignatureScannerProperties(detectOptions);
        resolveBomToolSearchProperties();

        updateDetectProperties(detectOptions);
    }

    private void resolveTildeInPaths() throws DetectUserFriendlyException {
        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RESOLVE_TILDE_IN_PATHS, PropertyAuthority.None)) {
            detectConfiguration.getCurrentProperties().keySet().stream()
                .forEach(it -> resolveTildeInDetectProperty(it));
        }
    }

    private void resolveTildeInDetectProperty(final DetectProperty detectProperty) {
        if (PropertyType.STRING == detectProperty.getPropertyType()) {
            final Optional<String> resolved = tildeInPathResolver.resolveTildeInValue(detectConfiguration.getProperty(detectProperty, PropertyAuthority.None));
            if (resolved.isPresent()) {
                detectConfiguration.setDetectProperty(detectProperty, resolved.get());
            }
        }
    }

    private void resolvePolicyProperties() {
        final String policyCheckFailOnSeverities = detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None);
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

    private void resolveSignatureScannerProperties(final List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        int hubSignatureScannerParallelProcessors = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, PropertyAuthority.None);
        if (hubSignatureScannerParallelProcessors == -1) {
            hubSignatureScannerParallelProcessors = Runtime.getRuntime().availableProcessors();
        }
        this.hubSignatureScannerParallelProcessors = hubSignatureScannerParallelProcessors;

        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None)) &&
                StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None))) {
            throw new DetectUserFriendlyException(
                "You have provided both a Black Duck signature scanner url AND a local Black Duck signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                ExitCodeType.FAILURE_GENERAL_ERROR);
        }
        final Boolean originalOfflineMode = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        hubOfflineMode = originalOfflineMode;
        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None))) {
            logger.info("A Black Duck signature scanner url was provided, which requires Black Duck offline mode. Setting Black Duck offline mode to true.");
            hubOfflineMode = true;
        }
        if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None))) {
            logger.info("A local Black Duck signature scanner path was provided, which requires Black Duck offline mode. Setting Black Duck offline mode to true.");
            hubOfflineMode = true;
        }
    }

    private void resolveBomToolSearchProperties() {
        bomToolSearchDirectoryExclusions = new ArrayList<>();
        for (final String exclusion : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, PropertyAuthority.None)) {
            bomToolSearchDirectoryExclusions.add(exclusion);
        }
        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS, PropertyAuthority.None)) {
            final List<String> defaultExcludedNames = Arrays.stream(DetectorSearchExcludedDirectories.values()).map(DetectorSearchExcludedDirectories::getDirectoryName).collect(Collectors.toList());
            bomToolSearchDirectoryExclusions.addAll(defaultExcludedNames);
        }
    }

    private void updateDetectProperties(final List<DetectOption> detectOptions) {
        updateOptionValue(detectOptions, DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, policyCheckFailOnSeverities);
        detectConfiguration.setDetectProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, policyCheckFailOnSeverities);

        updateOptionValue(detectOptions, DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, String.valueOf(hubSignatureScannerParallelProcessors));
        detectConfiguration.setDetectProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, String.valueOf(hubSignatureScannerParallelProcessors));

        updateOptionValue(detectOptions, DetectProperty.BLACKDUCK_OFFLINE_MODE, String.valueOf(hubOfflineMode));
        detectConfiguration.setDetectProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, String.valueOf(hubOfflineMode));

        updateOptionValue(detectOptions, DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, StringUtils.join(bomToolSearchDirectoryExclusions, ","));
        detectConfiguration.setDetectProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, StringUtils.join(bomToolSearchDirectoryExclusions, ","));

    }

    private void updateOptionValue(final List<DetectOption> detectOptions, final DetectProperty detectProperty, final String value) {
        detectOptions.stream().forEach(option -> {
            if (option.getDetectProperty() == detectProperty) {
                option.setPostInitValue(value);
            }
        });
    }

    @SuppressWarnings("unused")
    private void requestDeprecation(final List<DetectOption> detectOptions, final DetectProperty detectProperty) {
        detectOptions.stream().forEach(option -> {
            if (option.getDetectProperty() == detectProperty) {
                option.requestDeprecation();
            }
        });
    }

}
