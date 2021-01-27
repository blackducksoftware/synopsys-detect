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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class YarnLockParser {
    private static final String COMMENT_PREFIX = "#";
    private static final String[] VERSION_TOKENS = new String[] { "version \"", "version: " };

    private static final String OPTIONAL_DEPENDENCIES_TOKEN = "optionalDependencies:";
    private static final String PEER_DEPENDENCIES_TOKEN = "peerDependencies:"; // these dependencies have to be installed in the parent. Not actual dependencies as far as hub is concerned. - jp
    private static final String META_PEER_DEPENDENCIES_TOKEN = "peerDependenciesMeta:";
    private static final String META_DEPENDENCIES_TOKEN = "dependenciesMeta:";

    private enum YarnLockSections {
        DEPENDENCIES,
        META_DEPENDENCIES,
        OPTIONAL_DEPENDENCIES,
        PEER_DEPENDENCIES,
        META_PEER_DEPENDENCIES
    }

    public YarnLock parseYarnLock(List<String> yarnLockFileAsList) {
        List<YarnLockEntry> entries = new ArrayList<>();
        String resolvedVersion = "";
        Map<String, ParsedYarnLockDependency> currentDependencies = new HashMap<>();

        List<YarnLockEntryId> ids;
        YarnLockSections currentSection = YarnLockSections.DEPENDENCIES;

        List<String> cleanedYarnLockFileAsList = cleanList(yarnLockFileAsList);

        int indexOfFirstLevelZeroLine = findIndexOfFirstLevelZeroLine(cleanedYarnLockFileAsList);

        if (indexOfFirstLevelZeroLine == -1 || indexOfFirstLevelZeroLine == cleanedYarnLockFileAsList.size() - 1) {
            return new YarnLock(entries);
        }

        // We need to set ids with the first level zero line
        ids = parseMultipleEntryLine(cleanedYarnLockFileAsList.get(indexOfFirstLevelZeroLine));

        List<String> yarnLinesThatMatter = cleanedYarnLockFileAsList.subList(indexOfFirstLevelZeroLine + 1, cleanedYarnLockFileAsList.size());
        String currentMetaDependencyName = "";
        for (String line : yarnLinesThatMatter) {

            String trimmedLine = line.trim();
            int level = countIndent(line);
            if (level == 0) {
                entries.add(new YarnLockEntry(ids, resolvedVersion, currentDependencies.values().stream().map(ParsedYarnLockDependency::toDependency).collect(Collectors.toList())));
                resolvedVersion = "";
                currentDependencies.clear();
                currentSection = YarnLockSections.DEPENDENCIES;
                ids = parseMultipleEntryLine(line);
            } else if (level == 1 && StringUtils.startsWithAny(trimmedLine, VERSION_TOKENS)) {
                resolvedVersion = parseVersionFromLine(trimmedLine);
            } else if (level == 1 && trimmedLine.startsWith(OPTIONAL_DEPENDENCIES_TOKEN)) {
                currentSection = YarnLockSections.OPTIONAL_DEPENDENCIES;
            } else if (level == 1 && trimmedLine.startsWith(META_DEPENDENCIES_TOKEN)) {
                currentSection = YarnLockSections.META_DEPENDENCIES;
            } else if (level == 1 && trimmedLine.startsWith(PEER_DEPENDENCIES_TOKEN)) {
                currentSection = YarnLockSections.PEER_DEPENDENCIES;
            } else if (level == 1 && trimmedLine.startsWith(META_PEER_DEPENDENCIES_TOKEN)) {
                currentSection = YarnLockSections.META_PEER_DEPENDENCIES;
            } else if (level == 2 && currentSection == YarnLockSections.DEPENDENCIES || currentSection == YarnLockSections.OPTIONAL_DEPENDENCIES) {
                ParsedYarnLockDependency yarnLockDependency = parseDependencyFromLine(trimmedLine);
                if (currentSection == YarnLockSections.OPTIONAL_DEPENDENCIES) {
                    yarnLockDependency.setOptional(true);
                }
                currentDependencies.put(yarnLockDependency.getName(), yarnLockDependency);
            } else if (level == 2 && currentSection == YarnLockSections.META_DEPENDENCIES) {
                currentMetaDependencyName = parseMetaDependencyNameFromLine(line);
            } else if (level == 3 && currentSection == YarnLockSections.META_DEPENDENCIES) {
                if (line.contains("optional: true")) {
                    currentDependencies.get(currentMetaDependencyName).setOptional(true);
                }
            }
        }
        if (StringUtils.isNotBlank(resolvedVersion)) {
            entries.add(new YarnLockEntry(ids, resolvedVersion, Bds.of(currentDependencies.values()).map(ParsedYarnLockDependency::toDependency).toList()));
        }
        return new YarnLock(entries);
    }

    @NotNull
    private Integer findIndexOfFirstLevelZeroLine(List<String> cleanedYarnLockFileAsList) {
        return cleanedYarnLockFileAsList
                   .stream()
                   .filter(line -> countIndent(line) == 0)
                   .findFirst()
                   .map(cleanedYarnLockFileAsList::indexOf)
                   .orElse(-1);
    }

    @NotNull
    private List<String> cleanList(List<String> yarnLockFileAsList) {
        return yarnLockFileAsList
                   .stream()
                   .filter(StringUtils::isNotBlank)
                   .filter(line -> !line.trim().startsWith(COMMENT_PREFIX))
                   .collect(Collectors.toList());
    }

    public int countIndent(String line) {
        int count = 0;
        while (line.startsWith("  ")) {
            count++;
            line = line.substring(2);
        }
        return count;
    }

    private String parseMetaDependencyNameFromLine(String line) {
        return removeWrappingQuotes(StringUtils.substringBefore(line, ":"));
    }

    private ParsedYarnLockDependency parseDependencyFromLine(String line) {
        String[] pieces;
        if (line.contains(":")) {
            pieces = StringUtils.split(line, ":", 2);
        } else {
            pieces = StringUtils.split(line, " ", 2);
        }
        return new ParsedYarnLockDependency(removeWrappingQuotes(pieces[0]), removeWrappingQuotes(pieces[1]));
    }

    private String removeWrappingQuotes(String s) {
        return StringUtils.removeStart(StringUtils.removeEnd(s.trim(), "\""), "\"");
    }

    //Takes a line of the form "entry \"entry\" entry:"
    public List<YarnLockEntryId> parseMultipleEntryLine(String line) {
        List<YarnLockEntryId> ids = new ArrayList<>();
        String[] entries = line.split(",");
        for (String entryRaw : entries) {
            String entryNoColon = StringUtils.removeEnd(entryRaw.trim(), ":");
            String entryNoColonOrQuotes = removeWrappingQuotes(entryNoColon);
            YarnLockEntryId entry = parseSingleEntry(entryNoColonOrQuotes);
            ids.add(entry);
        }
        return ids;
    }

    //Takes an entry of format "name@version" or "@name@version" where name has an @ symbol.
    //Notice, this removes the workspace, so "name@workspace:version" will become simply "name@version"
    public YarnLockEntryId parseSingleEntry(String entry) {
        YarnLockEntryId normalEntry = parseSingleEntryNormally(entry);
        if (normalEntry.getVersion().contains(":")) {
            return new YarnLockEntryId(normalEntry.getName(), StringUtils.substringAfter(normalEntry.getVersion(), ":"));
        } else {
            return normalEntry;
        }
    }

    public YarnLockEntryId parseSingleEntryNormally(String entry) {
        if (StringUtils.countMatches(entry, "@") == 1 && entry.startsWith("@")) {
            return new YarnLockEntryId(entry, "");
        } else {
            String name = StringUtils.substringBeforeLast(entry, "@");
            String version = StringUtils.substringAfterLast(entry, "@");
            return new YarnLockEntryId(name, version);
        }
    }

    private String parseVersionFromLine(String line) {
        for (String token : VERSION_TOKENS) {
            if (line.startsWith(token)) {
                return removeWrappingQuotes(StringUtils.substringAfter(line, token));
            }
        }
        return line;
    }
}