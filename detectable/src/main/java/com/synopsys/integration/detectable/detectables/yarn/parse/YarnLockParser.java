/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class YarnLockParser {
    public static final String COMMENT_PREFIX = "#";
    public static final String VERSION_PREFIX = "version \"";
    public static final String VERSION_SUFFIX = "\"";

    private final YarnLineLevelParser lineLevelParser;

    public YarnLockParser(final YarnLineLevelParser lineLevelParser) {
        this.lineLevelParser = lineLevelParser;
    }

    public YarnLock parseYarnLock(final List<String> yarnLockFileAsList) {
        final Map<String, String> yarnLockResolvedVersions = new HashMap<>();

        final List<String> fuzzyIds = new ArrayList<>();
        for (final String line : yarnLockFileAsList) {
            if (StringUtils.isBlank(line) || line.trim().startsWith(COMMENT_PREFIX)) {
                continue;
            }

            final String trimmedLine = line.trim();
            final int level = lineLevelParser.parseIndentLevel(line);
            if (level == 0) {
                fuzzyIds.addAll(getFuzzyIdsFromLine(line));
            } else if (level == 1 && trimmedLine.startsWith(VERSION_PREFIX)) {
                final String resolvedVersion = trimmedLine.substring(VERSION_PREFIX.length(), trimmedLine.lastIndexOf(VERSION_SUFFIX));
                fuzzyIds.stream().forEach(fuzzyId -> yarnLockResolvedVersions.put(fuzzyId, resolvedVersion));
                fuzzyIds.clear();
            }
        }

        return new YarnLock(yarnLockResolvedVersions);
    }

    private List<String> getFuzzyIdsFromLine(final String s) {
        final String[] lines = s.split(",");
        return Arrays.stream(lines)
                   .map(line -> line.trim().replaceAll("\"", "").replaceAll(":", ""))
                   .collect(Collectors.toList());
    }
}
