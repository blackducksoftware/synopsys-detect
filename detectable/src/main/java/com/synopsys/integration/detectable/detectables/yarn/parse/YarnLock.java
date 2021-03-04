/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;

public class YarnLock {
    @Nullable
    private final String fileFormatVersion;
    private final List<YarnLockEntry> entries;

    public YarnLock(@Nullable String fileFormatVersion, List<YarnLockEntry> entries) {
        this.fileFormatVersion = fileFormatVersion;
        this.entries = entries;
    }

    public Optional<String> getFileFormatVersion() {
        return Optional.ofNullable(fileFormatVersion);
    }

    public List<YarnLockEntry> getEntries() {
        return entries;
    }
}
