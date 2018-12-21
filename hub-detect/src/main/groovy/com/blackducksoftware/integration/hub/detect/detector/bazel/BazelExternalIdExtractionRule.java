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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.util.List;

public class BazelExternalIdExtractionRule {
    // Everything following "bazel". Example: "query", "kind(.*, //external:*)", "--output", "xml"
    private final List<String> bazelQueryCommandArgsIncludingQuery;
    private final String ruleClassname;
    private final String ruleElementSelectorValue;
    // Example: ":"
    private final String artifactStringSeparatorRegex;

    public BazelExternalIdExtractionRule(final List<String> bazelQueryCommandArgsIncludingQuery, final String ruleClassname, final String ruleElementSelectorValue, final String artifactStringSeparatorRegex) {
        this.bazelQueryCommandArgsIncludingQuery = bazelQueryCommandArgsIncludingQuery;
        this.ruleClassname = ruleClassname;
        this.ruleElementSelectorValue = ruleElementSelectorValue;
        this.artifactStringSeparatorRegex = artifactStringSeparatorRegex;
    }

    public List<String> getBazelQueryCommandArgsIncludingQuery() {
        return bazelQueryCommandArgsIncludingQuery;
    }

    public String getRuleClassname() {
        return ruleClassname;
    }

    public String getRuleElementSelectorValue() {
        return ruleElementSelectorValue;
    }

    public String getArtifactStringSeparatorRegex() {
        return artifactStringSeparatorRegex;
    }
}
