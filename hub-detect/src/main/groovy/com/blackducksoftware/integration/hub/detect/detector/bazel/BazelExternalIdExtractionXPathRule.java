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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.util.Stringable;

public class BazelExternalIdExtractionXPathRule extends Stringable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // Everything following "bazel". Example: "query", "kind(.*, //external:*)", "--output", "xml"
    private final List<String> bazelQueryCommandArgsIncludingQuery;
    private final String xPathQuery;
    private final String ruleElementValueAttrName;
    // Example: ":"
    private final String artifactStringSeparatorRegex;

    public BazelExternalIdExtractionXPathRule(final List<String> bazelQueryCommandArgsIncludingQuery, final String xPathQuery, final String ruleElementValueAttrName, final String artifactStringSeparatorRegex) {
        this.bazelQueryCommandArgsIncludingQuery = bazelQueryCommandArgsIncludingQuery;
        this.xPathQuery = xPathQuery;
        this.ruleElementValueAttrName = ruleElementValueAttrName;
        this.artifactStringSeparatorRegex = artifactStringSeparatorRegex;
    }

    public BazelExternalIdExtractionXPathRule(final BazelExternalIdExtractionSimpleRule simpleRule) {
        this.bazelQueryCommandArgsIncludingQuery = simpleRule.getBazelQueryCommandArgsIncludingQuery();
        this.xPathQuery = String.format("/query/rule[@class='%s']/%s[@%s='%s']", simpleRule.getRuleClassname(), "string", "name", simpleRule.getRuleElementSelectorValue());
        logger.debug(String.format("Generated xPathQuery: %s", xPathQuery));
        this.ruleElementValueAttrName = "value";
        this.artifactStringSeparatorRegex = simpleRule.getArtifactStringSeparatorRegex();
    }

    public List<String> getBazelQueryCommandArgsIncludingQuery() {
        return bazelQueryCommandArgsIncludingQuery;
    }

    public String getXPathQuery() {
        return xPathQuery;
    }

    public String getRuleElementValueAttrName() {
        return ruleElementValueAttrName;
    }

    public String getArtifactStringSeparatorRegex() {
        return artifactStringSeparatorRegex;
    }
}
