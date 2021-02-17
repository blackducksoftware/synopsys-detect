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
package com.synopsys.integration.detectable.detectables.pear.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;

public class PearListParser {
    private static final String START_TOKEN = "=========";

    /**
     * @param pearListLines
     * @return A map of package names to their resolved versions
     */
    public Map<String, String> parse(final List<String> pearListLines) throws IntegrationException {
        final Map<String, String> dependenciesMap = new HashMap<>();

        boolean started = false;
        for (final String rawLine : pearListLines) {
            final String line = rawLine.trim();

            if (!started) {
                started = line.startsWith(START_TOKEN);
                continue;
            } else if (StringUtils.isBlank(line) || line.startsWith("Package")) {
                continue;
            }

            final String[] entry = line.split(" +");
            if (entry.length < 2) {
                throw new IntegrationException("Unable to parse pear list");
            }

            final String packageName = entry[0].trim();
            final String packageVersion = entry[1].trim();

            dependenciesMap.put(packageName, packageVersion);
        }

        return dependenciesMap;
    }
}
