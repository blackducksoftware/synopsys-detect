/**
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
package com.synopsys.integration.detectable.detectables.cran.parse;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class PackratDescriptionFileParser {
    private static final String PACKAGE_TOKEN = "Package:";
    private static final String VERSION_TOKEN = "Version:";

    public NameVersion getProjectNameVersion(final List<String> descriptionFileLines, final String defaultProjectName, final String defaultProjectVersion) {
        final NameVersion nameVersion = new NameVersion(defaultProjectName, defaultProjectVersion);

        for (final String rawLine : descriptionFileLines) {
            final String line = rawLine.trim();

            if (line.startsWith(PACKAGE_TOKEN)) {
                final String projectName = line.replace(PACKAGE_TOKEN, "").trim();
                nameVersion.setName(projectName);
            } else if (line.startsWith(VERSION_TOKEN)) {
                final String projectVersion = line.replace(VERSION_TOKEN, "").trim();
                nameVersion.setVersion(projectVersion);
            }
        }

        return nameVersion;
    }
}
