/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel.model;

import java.util.List;

import com.synopsys.integration.util.Stringable;

public class BazelExternalIdExtractionFullRule extends Stringable {
    // The args for the bazel query to get the target's dependencies
    private final List<String> targetDependenciesQueryBazelCmdArguments;
    // The search/replace transforms to run on the output of each targetDependenciesQuery to convert each into a bazel external ID
    private final List<SearchReplacePattern> dependencyToBazelExternalIdTransforms;
    // The args for the bazel query to get a dependency's details
    private final List<String> dependencyDetailsXmlQueryBazelCmdArguments;
    private final String xPathQuery;
    private final String ruleElementValueAttrName;
    // The separator between group, artifact, version in the bazel query output xml artifact value
    // Example: ":"
    private final String artifactStringSeparatorRegex;

    // Normal use case is to use RuleConverter.simpleToFull() to construct BazelExternalIdExtractionFullRule, not this ctor
    public BazelExternalIdExtractionFullRule(final List<String> targetDependenciesQueryBazelCmdArguments, final List<SearchReplacePattern> dependencyToBazelExternalIdTransforms,
        final List<String> dependencyDetailsXmlQueryBazelCmdArguments,
        final String xPathQuery, final String ruleElementValueAttrName, final String artifactStringSeparatorRegex) {
        this.targetDependenciesQueryBazelCmdArguments = targetDependenciesQueryBazelCmdArguments;
        this.dependencyToBazelExternalIdTransforms = dependencyToBazelExternalIdTransforms;
        this.dependencyDetailsXmlQueryBazelCmdArguments = dependencyDetailsXmlQueryBazelCmdArguments;
        this.xPathQuery = xPathQuery;
        this.ruleElementValueAttrName = ruleElementValueAttrName;
        this.artifactStringSeparatorRegex = artifactStringSeparatorRegex;
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
