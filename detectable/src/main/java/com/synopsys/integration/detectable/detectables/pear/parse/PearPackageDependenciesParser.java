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
package com.synopsys.integration.detectable.detectables.pear.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.exception.IntegrationException;

public class PearPackageDependenciesParser {
    private static final String START_TOKEN = "=========";

    public List<PackageDependency> parse(final List<String> packageDependenciesLines) throws IntegrationException {
        final List<PackageDependency> packageDependencies = new ArrayList<>();

        boolean started = false;
        for (final String rawLine : packageDependenciesLines) {
            final String line = rawLine.trim();

            if (!started) {
                started = line.startsWith(START_TOKEN);
                continue;
            } else if (StringUtils.isBlank(line) || line.startsWith("Required") || line.startsWith("REQUIRED")) {
                continue;
            }

            final String[] entry = line.split(" +");
            if (entry.length < 3) {
                throw new IntegrationException("Unable to parse package-dependencies");
            }

            final boolean required = BooleanUtils.toBoolean(entry[0]);
            final String type = entry[1].trim();
            final String[] namePieces = entry[2].split("/");
            final String name = namePieces[namePieces.length - 1].trim();

            if ("Package".equalsIgnoreCase(type)) {
                final PackageDependency packageDependency = new PackageDependency(name, required);
                packageDependencies.add(packageDependency);
            }
        }

        return packageDependencies;
    }
}
