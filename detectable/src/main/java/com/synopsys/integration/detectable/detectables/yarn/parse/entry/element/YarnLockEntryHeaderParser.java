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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class YarnLockEntryHeaderParser implements YarnLockElementTypeParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockEntryHeaderParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    @Override
    public boolean applies(String elementLine) {
        if (elementLine.startsWith("#") || (elementLine.trim().length() == 0) || elementLine.startsWith(" ") || elementLine.startsWith("\t")) {
            return false;
        }
        return true;
    }

    @Override
    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int bodyElementLineIndex) {
        String line = yarnLockLines.get(bodyElementLineIndex);
        // TODO make this more consistent with other (new) parsing? Use StringTokenizer perhaps?
        String[] entries = line.split(",");
        for (String entryRaw : entries) {
            String entryNoColon = StringUtils.removeEnd(entryRaw.trim(), ":");
            String entryNoColonOrQuotes = yarnLockLineAnalyzer.unquote(entryNoColon);
            YarnLockEntryId entry = parseSingleEntry(entryNoColonOrQuotes);
            entryBuilder.addId(entry);
        }
        return bodyElementLineIndex;
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
