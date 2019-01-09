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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class BazelExternalIdGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final String bazelExe;
    private final BazelQueryXmlOutputParser parser;
    private final File workspaceDir;
    private final String bazelTarget;
    private final Map<BazelExternalIdExtractionFullRule, Exception> exceptionsGenerated = new HashMap<>();

    public BazelExternalIdGenerator(final ExecutableRunner executableRunner, final String bazelExe,
        final BazelQueryXmlOutputParser parser, final File workspaceDir, final String bazelTarget) {
        this.executableRunner = executableRunner;
        this.bazelExe = bazelExe;
        this.parser = parser;
        this.workspaceDir = workspaceDir;
        this.bazelTarget = bazelTarget;
    }

    public List<BazelExternalId> generate(BazelExternalIdExtractionFullRule xPathRule) {
        final List<BazelExternalId> projectExternalIds = new ArrayList<>();
        final BazelVariableSubstitutor targetOnlyVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget);
        ExecutableOutput targetDependenciesQueryResults = null;
        try {
            targetDependenciesQueryResults = executableRunner.executeQuietly(workspaceDir, bazelExe, targetOnlyVariableSubstitutor.substitute(xPathRule.getTargetDependenciesQueryBazelCmdArguments()));
        } catch (ExecutableRunnerException e) {
            logger.error(String.format("Error executing bazel with args: %s: %s", xPathRule.getTargetDependenciesQueryBazelCmdArguments(), e.getMessage()));
            exceptionsGenerated.put(xPathRule, e);
            return projectExternalIds;
        }
        final int targetDependenciesQueryReturnCode = targetDependenciesQueryResults.getReturnCode();
        final String targetDependenciesQueryOutput = targetDependenciesQueryResults.getStandardOutput();
        logger.debug(String.format("Bazel targetDependenciesQuery returned %d; output: %s", targetDependenciesQueryReturnCode, targetDependenciesQueryOutput));

        // TODO : error checking?
        // TODO : streams
        // TODO : split up this method
        final String[] rawDependencies = targetDependenciesQueryOutput.split("\\s+");
        for (final String rawDependency : rawDependencies) {
            String bazelExternalId = rawDependencyToBazelExternalId(xPathRule, rawDependency);
            final List<String> dependencyDetailsQueryArgs = bazelExternalIdToDependencyDetailsQueryArgs(xPathRule, bazelExternalId);


            ExecutableOutput dependencyDetailsXmlQueryResults = null;
            try {
                dependencyDetailsXmlQueryResults = executableRunner.executeQuietly(workspaceDir, bazelExe, dependencyDetailsQueryArgs);
            } catch (ExecutableRunnerException e) {
                logger.error(String.format("Error executing bazel with args: %s: %s", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments(), e.getMessage()));
                exceptionsGenerated.put(xPathRule, e);
                return projectExternalIds;
            }
            final int dependencyDetailsXmlQueryReturnCode = dependencyDetailsXmlQueryResults.getReturnCode();
            final String dependencyDetailsXmlQueryOutput = dependencyDetailsXmlQueryResults.getStandardOutput();
            logger.debug(String.format("Bazel targetDependenciesQuery returned %d; output: %s", dependencyDetailsXmlQueryReturnCode, dependencyDetailsXmlQueryOutput));

            final String xml = dependencyDetailsXmlQueryResults.getStandardOutput();
            logger.debug(String.format("Bazel query returned %d; output: %s", dependencyDetailsXmlQueryReturnCode, xml));


            final List<String> ruleArtifactStrings;
            try {
                ruleArtifactStrings = parser.parseStringValuesWithXPath(xml, xPathRule.getXPathQuery(), xPathRule.getRuleElementValueAttrName());
            } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
                logger.error(String.format("Error parsing bazel query output with: %s: %s", xPathRule.getXPathQuery(), e.getMessage()));
                exceptionsGenerated.put(xPathRule, e);
                return projectExternalIds;
            }
            for (String artifactString : ruleArtifactStrings) {
                projectExternalIds.add(BazelExternalId.fromBazelArtifactString(artifactString, xPathRule.getArtifactStringSeparatorRegex()));
            }
        }
        return projectExternalIds;
    }

    private List<String> bazelExternalIdToDependencyDetailsQueryArgs(final BazelExternalIdExtractionFullRule xPathRule, final String bazelExternalId) {
        final BazelVariableSubstitutor dependencyVariableSubstitutor = new BazelVariableSubstitutor(bazelTarget, bazelExternalId);
        return dependencyVariableSubstitutor.substitute(xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments());
    }

    private String rawDependencyToBazelExternalId(final BazelExternalIdExtractionFullRule xPathRule, final String rawDependency) {
        logger.debug(String.format("Processing rawDependency: %s", rawDependency));
        String bazelExternalId = rawDependency;
        for (SearchReplacePattern pattern : xPathRule.getDependencyToBazelExternalIdTransforms()) {
            logger.debug(String.format("Replacing %s with %s", pattern.getSearchRegex(), pattern.getReplacementString()));
            bazelExternalId = bazelExternalId.replaceAll(pattern.getSearchRegex(), pattern.getReplacementString());
        }
        logger.debug(String.format("Transformed rawDependency: %s to bazel external id %s", rawDependency, bazelExternalId));
        return bazelExternalId;
    }

    public boolean isErrors() {
        if (exceptionsGenerated.keySet().size() > 0) {
            return true;
        }
        return false;
    }

    public String getErrorMessage() {
        if (!isErrors()) {
            return "No errors";
        }
        final StringBuilder sb = new StringBuilder("Errors encountered generating external IDs: ");
        for (BazelExternalIdExtractionFullRule rule : exceptionsGenerated.keySet()) {
            sb.append(String.format("%s: %s; ", rule, exceptionsGenerated.get(rule).getMessage()));
        }
        return sb.toString();
    }
}
