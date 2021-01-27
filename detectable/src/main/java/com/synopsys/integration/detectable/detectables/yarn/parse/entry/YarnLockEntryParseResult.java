package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

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

    public YarnLockEntry getYarnLockEntry() {
        return yarnLockEntry;
    }
}
