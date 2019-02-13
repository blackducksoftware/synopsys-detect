/**
 * detect-application
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
package com.synopsys.integration.detect.tool.bazel;

import com.synopsys.integration.util.Stringable;

public class BazelExternalIdExtractionSimpleRule extends Stringable {
    // Example: "@.*:jar"
    private final String targetDependenciesQueryFilterPattern;

    // Location of artifact in dependency details XML:
    // Example: "maven_jar"
    private final String ruleClassname;
    // Example: "artifact"
    private final String ruleElementSelectorValue;
    // Example: ":"
    private final String artifactStringSeparatorRegex;

    public BazelExternalIdExtractionSimpleRule(final String targetDependenciesQueryFilterPattern, final String ruleClassname,
        final String ruleElementSelectorValue, final String artifactStringSeparatorRegex) {
        this.targetDependenciesQueryFilterPattern = targetDependenciesQueryFilterPattern;
        this.ruleClassname = ruleClassname;
        this.ruleElementSelectorValue = ruleElementSelectorValue;
        this.artifactStringSeparatorRegex = artifactStringSeparatorRegex;
    }

    public String getTargetDependenciesQueryFilterPattern() {
        return targetDependenciesQueryFilterPattern;
    }

    public String getDependencyDetailsXmlQueryKindPattern() {
        return ruleClassname;
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
