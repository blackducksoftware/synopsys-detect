package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.Optional;

public class YarnLockEntryParseResult {
    private final int lastParsedLineIndex;
    private final YarnLockEntry yarnLockEntry;

    public YarnLockEntryParseResult(int lastParsedLineIndex, YarnLockEntry yarnLockEntry) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.yarnLockEntry = yarnLockEntry;
    }

    public YarnLockEntryParseResult(int lastParsedLineIndex) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.yarnLockEntry = null;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<YarnLockEntry> getYarnLockEntry() {
        return Optional.ofNullable(yarnLockEntry);
    }
}
