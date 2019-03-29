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
package com.synopsys.integration.detectable.detectables.bazel.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionFullRule;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionSimpleRule;
import com.synopsys.integration.detectable.detectables.bazel.model.SearchReplacePattern;

public class RuleConverter {
    public static final String BAZEL_QUERY_SUBCOMMAND = "query";
    public static final String FILTER_GET_DEPENDENCIES_FOR_TARGET = "filter(\"%s\", deps(${detect.bazel.target}))";
    public static final String FILTER_GET_DETAILS_FOR_DEPENDENCY = "kind(%s, ${detect.bazel.target.dependency})";
    public static final String OUTPUT_SELECTOR = "--output";
    public static final String OUTPUT_XML_FORMAT = "xml";
    public static final String XPATH_QUERY_FOR_ARTIFACT = "/query/rule[@class='%s']/%s[@%s='%s']";
    public static final String XPATH_QUERY_ARTIFACT_VALUE_ATTRIBUTE = "value";
    public static final String XPATH_QUERY_RULE_ELEMENT_CLASS = "string";
    public static final String XPATH_QUERY_SELECTOR_ATTRIBUTE = "name";

    public static BazelExternalIdExtractionFullRule simpleToFull(final BazelExternalIdExtractionSimpleRule simpleRule) {

        final List<String> targetDependenciesQueryBazelCmdArguments = Arrays.asList(BAZEL_QUERY_SUBCOMMAND,
            String.format(FILTER_GET_DEPENDENCIES_FOR_TARGET, simpleRule.getTargetDependenciesQueryFilterPattern()));

        final List<SearchReplacePattern> dependencyToBazelExternalIdTransforms = new ArrayList<>();
        dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("^@", ""));
        dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("//.*", ""));
        dependencyToBazelExternalIdTransforms.add(new SearchReplacePattern("^", "//external:"));

        final List<String> dependencyDetailsXmlQueryBazelCmdArguments = Arrays.asList(BAZEL_QUERY_SUBCOMMAND,
            String.format(FILTER_GET_DETAILS_FOR_DEPENDENCY, simpleRule.getDependencyDetailsXmlQueryKindPattern()),
            OUTPUT_SELECTOR, OUTPUT_XML_FORMAT);

        final String xPathQuery = String.format(XPATH_QUERY_FOR_ARTIFACT, simpleRule.getRuleClassname(), XPATH_QUERY_RULE_ELEMENT_CLASS, XPATH_QUERY_SELECTOR_ATTRIBUTE, simpleRule.getRuleElementSelectorValue());
        final String ruleElementValueAttrName = XPATH_QUERY_ARTIFACT_VALUE_ATTRIBUTE;
        final String artifactStringSeparatorRegex = simpleRule.getArtifactStringSeparatorRegex();

        final BazelExternalIdExtractionFullRule fullRule = new BazelExternalIdExtractionFullRule(targetDependenciesQueryBazelCmdArguments,
        dependencyToBazelExternalIdTransforms,
        dependencyDetailsXmlQueryBazelCmdArguments,
        xPathQuery, ruleElementValueAttrName,
        artifactStringSeparatorRegex);
        return fullRule;
    }
}
