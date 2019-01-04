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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

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
    private final Map<BazelExternalIdExtractionXPathRule, Exception> exceptionsGenerated = new HashMap<>();

    public BazelExternalIdGenerator(final ExecutableRunner executableRunner, final String bazelExe,
        final BazelQueryXmlOutputParser parser, final File workspaceDir) {
        this.executableRunner = executableRunner;
        this.bazelExe = bazelExe;
        this.parser = parser;
        this.workspaceDir = workspaceDir;
    }

    public List<BazelExternalId> generate(BazelExternalIdExtractionXPathRule xPathRule) {
        final List<BazelExternalId> projectExternalIds = new ArrayList<>();
        ExecutableOutput bazelQueryOutput = null;
        try {
            bazelQueryOutput = executableRunner.executeQuietly(workspaceDir, bazelExe, xPathRule.getBazelQueryCommandArgsIncludingQuery());
        } catch (ExecutableRunnerException e) {
            logger.error(String.format("Error executing bazel with args: %s: %s", xPathRule.getBazelQueryCommandArgsIncludingQuery(), e.getMessage()));
            exceptionsGenerated.put(xPathRule, e);
            return projectExternalIds;
        }
        final int returnCode = bazelQueryOutput.getReturnCode();
        final String xml = bazelQueryOutput.getStandardOutput();
        logger.trace(String.format("Bazel query returned %d; output: %s", returnCode, xml));
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
        return projectExternalIds;
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
        for (BazelExternalIdExtractionXPathRule rule : exceptionsGenerated.keySet()) {
            sb.append(String.format("%s: %s; ", rule, exceptionsGenerated.get(rule).getMessage()));
        }
        return sb.toString();
    }
}
