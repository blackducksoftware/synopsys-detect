/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class YarnLockEntryParseResult {
    private final int lastParsedLineIndex;
    @Nullable
    private final YarnLockEntry yarnLockEntry;

    public YarnLockEntryParseResult(int lastParsedLineIndex, @Nullable YarnLockEntry yarnLockEntry) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.yarnLockEntry = yarnLockEntry;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<YarnLockEntry> getYarnLockEntry() {
        return Optional.ofNullable(yarnLockEntry);
    }
}
