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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.util.Stringable;

public class BazelExternalIdExtractionXPathRule extends Stringable {
    // The bazel query to get the target's dependencies
    // Include everything following "bazel". Example: "query", "filter(\"@.*:jar\", deps(${detect.bazel.target}))"
    private final List<String> targetDependenciesQueryBazelCmdArguments;
    // The search/replace transforms to run on the output of each targetDependenciesQuery to convert each into a bazel external ID
    private final List<SearchReplacePattern> dependencyToBazelExternalIdTransforms;
    //
    private final List<String> dependencyDetailsXmlQueryBazelCmdArguments;
    private final String xPathQuery;
    private final String ruleElementValueAttrName;
    // Example: ":"
    private final String artifactStringSeparatorRegex;

    public BazelExternalIdExtractionXPathRule(final List<String> targetDependenciesQueryBazelCmdArguments, final List<SearchReplacePattern> dependencyToBazelExternalIdTransforms,
        final List<String> dependencyDetailsXmlQueryBazelCmdArguments,
        final String xPathQuery, final String ruleElementValueAttrName, final String artifactStringSeparatorRegex) {
        this.targetDependenciesQueryBazelCmdArguments = targetDependenciesQueryBazelCmdArguments;
        this.dependencyToBazelExternalIdTransforms = dependencyToBazelExternalIdTransforms;
        this.dependencyDetailsXmlQueryBazelCmdArguments = dependencyDetailsXmlQueryBazelCmdArguments;
        this.xPathQuery = xPathQuery;
        this.ruleElementValueAttrName = ruleElementValueAttrName;
        this.artifactStringSeparatorRegex = artifactStringSeparatorRegex;
    }

    public BazelExternalIdExtractionXPathRule(final BazelExternalIdExtractionSimpleRule simpleRule) {
        this.targetDependenciesQueryBazelCmdArguments = Arrays.asList("query",
            String.format("filter(\"%s\", deps(${detect.bazel.target}))", simpleRule.getTargetDependenciesQueryFilterPattern()));

        this.dependencyToBazelExternalIdTransforms = new ArrayList<>();
        this.dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("^@", ""));
        this.dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("//.*", ""));
        this.dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("^", "//external:"));

        this.dependencyDetailsXmlQueryBazelCmdArguments = Arrays.asList("query",
            String.format("kind(%s, ${detect.bazel.target.dependency})", simpleRule.getDependencyDetailsXmlQueryKindPattern()),
            "--output", "xml");

        this.xPathQuery = String.format("/query/rule[@class='%s']/%s[@%s='%s']", simpleRule.getRuleClassname(), "string", "name", simpleRule.getRuleElementSelectorValue());
        this.ruleElementValueAttrName = "value";
        this.artifactStringSeparatorRegex = simpleRule.getArtifactStringSeparatorRegex();
    }

    public List<String> getTargetDependenciesQueryBazelCmdArguments() {
        return targetDependenciesQueryBazelCmdArguments;
    }

    public List<SearchReplacePattern> getDependencyToBazelExternalIdTransforms() {
        return dependencyToBazelExternalIdTransforms;
    }

    public List<String> getDependencyDetailsXmlQueryBazelCmdArguments() {
        return dependencyDetailsXmlQueryBazelCmdArguments;
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
