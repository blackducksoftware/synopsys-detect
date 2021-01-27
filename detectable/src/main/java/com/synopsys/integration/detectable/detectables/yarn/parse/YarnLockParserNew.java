package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParseResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;

public class YarnLockParserNew {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockEntryParser yarnLockNodeParser;

    public YarnLockParserNew(YarnLockEntryParser yarnLockNodeParser) {
        this.yarnLockNodeParser = yarnLockNodeParser;
    }

    public YarnLock parseYarnLock(List<String> yarnLockFileAsList) {
        int lineIndex = 0;
        while (lineIndex < yarnLockFileAsList.size()) {
            String line = yarnLockFileAsList.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, line);
            // Parse the entire node
            YarnLockEntryParseResult entryParseResult = yarnLockNodeParser.parseEntry(yarnLockFileAsList, lineIndex);
        }
        return new YarnLock(new ArrayList<>(0));
    }
}
