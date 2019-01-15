/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.tool.bazel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectConfiguration detectConfiguration;
    private final ExecutableRunner executableRunner;
    private final BazelQueryXmlOutputParser parser;
    private final BazelExternalIdExtractionSimpleRules simpleRules;
    private final BazelCodeLocationBuilder codeLocationGenerator;
    private final BazelExternalIdExtractionFullRuleJsonProcessor bazelExternalIdExtractionFullRuleJsonProcessor;

    public BazelExtractor(final DetectConfiguration detectConfiguration, final ExecutableRunner executableRunner, BazelQueryXmlOutputParser parser, final BazelExternalIdExtractionSimpleRules simpleRules,
        final BazelCodeLocationBuilder codeLocationGenerator, final BazelExternalIdExtractionFullRuleJsonProcessor bazelExternalIdExtractionFullRuleJsonProcessor) {
        this.detectConfiguration = detectConfiguration;
        this.executableRunner = executableRunner;
        this.parser = parser;
        this.simpleRules = simpleRules;
        this.codeLocationGenerator = codeLocationGenerator;
        this.bazelExternalIdExtractionFullRuleJsonProcessor = bazelExternalIdExtractionFullRuleJsonProcessor;
    }

    public Extraction extract(final String bazelExe, final File workspaceDir) {
        logger.debug("Bazel extractAndPublishResults()");
        try {
            codeLocationGenerator.setWorkspaceDir(workspaceDir);
            final String fullRulesPath = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_ADVANCED_RULES_PATH, PropertyAuthority.None);
            final String bazelTarget = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_TARGET, PropertyAuthority.None);
            List<BazelExternalIdExtractionFullRule> fullRules;
            if (StringUtils.isNotBlank(fullRulesPath)) {
                fullRules = loadXPathRulesFromFile(fullRulesPath);
                logger.debug(String.format("Read %d rule(s) from %s", fullRules.size(), fullRulesPath));
            } else {
                fullRules = simpleRules.getRules().stream()
                                      .map(BazelExternalIdExtractionFullRule::new).collect(Collectors.toList());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Using default rules:\n%s", bazelExternalIdExtractionFullRuleJsonProcessor.toJson(fullRules)));
                }
            }
            BazelExternalIdGenerator externalIdGenerator = new BazelExternalIdGenerator(executableRunner, bazelExe, parser, workspaceDir, bazelTarget);
            fullRules.stream()
                .map(externalIdGenerator::generate)
                .flatMap(Collection::stream)
                .forEach(codeLocationGenerator::addDependency);
            if (externalIdGenerator.isErrors()) {
                return new Extraction.Builder().failure(externalIdGenerator.getErrorMessage()).build();
            }
            final List<DetectCodeLocation> codeLocations = codeLocationGenerator.build();
            final String projectName = cleanProjectName(bazelTarget);
            final Extraction.Builder builder = new Extraction.Builder()
                                                   .success(codeLocations)
                                                   .projectName(projectName);
            return builder.build();
        } catch (Exception e) {
            final String msg = String.format("Bazel processing exception: %s", e.getMessage());
            logger.debug(msg, e);
            return new Extraction.Builder().failure(msg).build();
        }
    }

    private String cleanProjectName(final String bazelTarget) {
        String projectName = bazelTarget
                                 .replaceAll("^//", "")
                   .replaceAll("^:", "")
                   .replaceAll("/", "_")
                   .replaceAll(":", "_");
        return projectName;
    }

    private List<BazelExternalIdExtractionFullRule> loadXPathRulesFromFile(final String xPathRulesJsonFilePath) throws IOException {
        final File jsonFile = new File(xPathRulesJsonFilePath);
        List<BazelExternalIdExtractionFullRule> loadedRules = bazelExternalIdExtractionFullRuleJsonProcessor.load(jsonFile);
        return loadedRules;
    }
}
