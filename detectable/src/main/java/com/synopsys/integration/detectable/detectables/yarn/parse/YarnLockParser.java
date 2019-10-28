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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class YarnLockParser {
    private static final String COMMENT_PREFIX = "#";
    private static final String VERSION_PREFIX = "version \"";
    private static final String VERSION_SUFFIX = "\"";
    private static final String OPTIONAL_DEPENDENCIES_TOKEN = "optionalDependencies:";

    public YarnLock parseYarnLock(final List<String> yarnLockFileAsList) {
        boolean started = false;
        final List<YarnLockEntry> entries = new ArrayList<>();
        String resolvedVersion = "";
        List<YarnLockDependency> dependencies = new ArrayList<>();
        List<YarnLockEntryId> ids = new ArrayList<>();
        boolean inOptionalDependencies = false;

        for (final String line : yarnLockFileAsList) {
            if (StringUtils.isBlank(line) || line.trim().startsWith(COMMENT_PREFIX)) {
                continue;
            }

            final String trimmedLine = line.trim();
            final int level = countIndent(line);
            if (level == 0) {
                if (started) {
                    entries.add(new YarnLockEntry(ids, resolvedVersion, dependencies));
                    resolvedVersion = "";
                    dependencies = new ArrayList<>();
                    inOptionalDependencies = false;
                } else {
                    started = true;
                }
                ids = getFuzzyIdsFromLine(line);
            } else if (level == 1 && trimmedLine.startsWith(VERSION_PREFIX)) {
                resolvedVersion = getVersionFromLine(trimmedLine);
            } else if (level == 1 && trimmedLine.startsWith(OPTIONAL_DEPENDENCIES_TOKEN)) {
                inOptionalDependencies = true;
            } else if (level == 2) {
                dependencies.add(getDependencyFromLine(trimmedLine, inOptionalDependencies));
            }
        }
        if (StringUtils.isNotBlank(resolvedVersion)) {
            entries.add(new YarnLockEntry(ids, resolvedVersion, dependencies));
        }

        return new YarnLock(entries);
    }

    public int countIndent(String line) {
        int count = 0;
        while (line.startsWith("  ")) {
            count++;
            line = line.substring(2);
        }
        return count;
    }

    private YarnLockDependency getDependencyFromLine(final String line, final boolean optional) {
        final String[] pieces = StringUtils.split(line, " ", 2);
        return new YarnLockDependency(removeWrappingQuotes(pieces[0]), removeWrappingQuotes(pieces[1]), optional);
    }

    private String removeWrappingQuotes(final String s) {
        return StringUtils.removeStart(StringUtils.removeEnd(s.trim(), "\""), "\"");
    }

    private List<YarnLockEntryId> getFuzzyIdsFromLine(final String s) {
        final List<YarnLockEntryId> ids = new ArrayList<>();
        final String[] lines = s.split(",");
        for (final String line : lines) {
            final String cleanedLine = removeWrappingQuotes(StringUtils.removeEnd(line.trim(), ":"));
            final int last = cleanedLine.trim().lastIndexOf('@');
            final String name = cleanedLine.substring(0, last);
            final String version = cleanedLine.substring(last + 1);
            ids.add(new YarnLockEntryId(name, version));
        }
        return ids;
    }

    private String getVersionFromLine(final String line) {
        final String rawVersion = line.substring(VERSION_PREFIX.length(), line.lastIndexOf(VERSION_SUFFIX));
        return removeWrappingQuotes(rawVersion);
    }
}
