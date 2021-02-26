/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParseResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;

public class YarnLockParser {
    private final YarnLockEntryParser yarnLockEntryParser;

    public YarnLockParser(YarnLockEntryParser yarnLockEntryParser) {
        this.yarnLockEntryParser = yarnLockEntryParser;
    }

    public YarnLock parseYarnLock(List<String> yarnLockFileAsList) {
        List<YarnLockEntry> entries = new ArrayList<>();
        int lineIndex = 0;
        while (lineIndex < yarnLockFileAsList.size()) {
            YarnLockEntryParseResult entryParseResult = yarnLockEntryParser.parseNextEntry(yarnLockFileAsList, lineIndex);
            entryParseResult.getYarnLockEntry().ifPresent(entries::add);
            lineIndex = entryParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        return new YarnLock(entries);
    }
}
