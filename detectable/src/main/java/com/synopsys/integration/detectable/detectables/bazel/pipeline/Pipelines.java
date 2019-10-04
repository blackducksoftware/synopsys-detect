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
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.bazel.model.Step;

public class Pipelines {
    private final Map<String, List<Step>> p = new HashMap<>();

    public Pipelines() {
        final List<Step> mavenJarPipeline = new ArrayList<>();
        mavenJarPipeline.add(new Step("executeBazelOnEach", Arrays.asList("query", "filter('@.*:jar', deps(${detect.bazel.target}))")));
        mavenJarPipeline.add(new Step("splitEach", Arrays.asList("\\s+")));
        mavenJarPipeline.add(new Step("edit", Arrays.asList("^@", "")));
        mavenJarPipeline.add(new Step("edit", Arrays.asList("//.*", "")));
        mavenJarPipeline.add(new Step("edit", Arrays.asList("^", "//external:")));
        mavenJarPipeline.add(new Step("executeBazelOnEach", Arrays.asList("query", "kind(maven_jar, ${0})", "--output", "xml")));
        mavenJarPipeline.add(new Step("parseEachXml", Arrays.asList("/query/rule[@class='maven_jar']/string[@name='artifact']", "value")));
        p.put("maven_jar", mavenJarPipeline);

        final List<Step> mavenInstallPipeline = new ArrayList<>();
        mavenInstallPipeline.add(new Step("executeBazelOnEach", Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(${detect.bazel.target}))", "--output", "build")));
        mavenInstallPipeline.add(new Step("splitEach", Arrays.asList("\n")));
        mavenInstallPipeline.add(new Step("filter", Arrays.asList(".*maven_coordinates=.*")));
        mavenInstallPipeline.add(new Step("edit", Arrays.asList("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", "")));
        mavenInstallPipeline.add(new Step("edit", Arrays.asList("\".*", "")));
        p.put("maven_install", mavenInstallPipeline);
    }

    public Optional<List<Step>> select(final String bazelDependencyType) {
        final List<Step> pipeline = p.get(bazelDependencyType);
        if (pipeline == null) {
            return Optional.empty();
        }
        return Optional.of(pipeline);
    }
}
