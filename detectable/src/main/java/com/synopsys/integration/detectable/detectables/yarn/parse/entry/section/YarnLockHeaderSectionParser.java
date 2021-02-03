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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class YarnLockHeaderSectionParser implements YarnLockEntrySectionParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockHeaderSectionParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    @Override
    public boolean applies(String elementLine) {
        return yarnLockLineAnalyzer.measureIndentDepth(elementLine) == 0;
    }

    @Override
    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection) {
        String line = yarnLockLines.get(lineIndexOfStartOfSection);
        StringTokenizer tokenizer = TokenizerFactory.createHeaderTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String rawEntryString = tokenizer.nextToken().trim();
            String entryString = StringUtils.removeEnd(rawEntryString, ":").trim();
            String unquotedEntryString = yarnLockLineAnalyzer.unquote(entryString);
            YarnLockEntryId entry = parseSingleEntry(unquotedEntryString);
            logger.trace("Entry header ID: name: {}, version: {}", entry.getName(), entry.getVersion());
            entryBuilder.addId(entry);
        }
        return lineIndexOfStartOfSection;
    }

    //Takes an entry of format "name@version" or "@name@version" where name has an @ symbol.
    //Notice, this removes the workspace, so "name@workspace:version" will become simply "name@version"
    private YarnLockEntryId parseSingleEntry(String entry) {
        YarnLockEntryId normalEntry = parseSingleEntryNormally(entry);
        if (normalEntry.getVersion().contains(":")) {
            return new YarnLockEntryId(normalEntry.getName(), StringUtils.substringAfter(normalEntry.getVersion(), ":"));
        } else {
            return normalEntry;
        }
    }

    private YarnLockEntryId parseSingleEntryNormally(String entry) {
        if (StringUtils.countMatches(entry, "@") == 1 && entry.startsWith("@")) {
            return new YarnLockEntryId(entry, "");
        } else {
            String name = StringUtils.substringBeforeLast(entry, "@");
            String version = StringUtils.substringAfterLast(entry, "@");
            return new YarnLockEntryId(name, version);
        }
    }
}
