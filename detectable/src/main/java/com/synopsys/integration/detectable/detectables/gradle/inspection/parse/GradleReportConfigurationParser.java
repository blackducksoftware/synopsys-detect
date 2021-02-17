/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;

public class GradleReportConfigurationParser {
    private final GradleReportLineParser parser = new GradleReportLineParser();

    public GradleConfiguration parse(final String header, final List<String> dependencyLines) {
        final GradleConfiguration configuration = new GradleConfiguration();

        configuration.setName(parseConfigurationName(header));

        configuration.setChildren(dependencyLines.stream()
                                      .map(parser::parseLine)
                                      .collect(Collectors.toList()));

        return configuration;
    }

    private String parseConfigurationName(final String header) {
        if (header.contains(" - ")) {
            return header.substring(0, header.indexOf(" - ")).trim();
        } else {
            return header.trim();
        }
    }

}
