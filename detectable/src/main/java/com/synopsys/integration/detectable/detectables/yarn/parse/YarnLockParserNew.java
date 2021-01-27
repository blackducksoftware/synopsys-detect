package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParseResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;

public class YarnLockParserNew {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockEntryParser yarnLockNodeParser;

    public YarnLockParserNew(YarnLockEntryParser yarnLockNodeParser) {
        this.yarnLockNodeParser = yarnLockNodeParser;
    }

    public YarnLock parseYarnLock(List<String> yarnLockFileAsList) {
        List<YarnLockEntry> entries = new ArrayList<>();
        int lineIndex = 0;
        while (lineIndex < yarnLockFileAsList.size()) {
            String line = yarnLockFileAsList.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, line);
            // Parse the entire entry
            YarnLockEntryParseResult entryParseResult = yarnLockNodeParser.parseEntry(yarnLockFileAsList, lineIndex);
            entryParseResult.getYarnLockEntry().ifPresent(entry -> entries.add(entry));
            lineIndex = entryParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        return new YarnLock(entries);
    }
}
