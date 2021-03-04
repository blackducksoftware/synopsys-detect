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
    private final boolean yarn2Project;
    private final List<YarnLockEntry> entries;

    public YarnLock(@Nullable String fileFormatVersion, boolean yarn2Project, List<YarnLockEntry> entries) {
        this.fileFormatVersion = fileFormatVersion;
        this.yarn2Project = yarn2Project;
        this.entries = entries;
    }

    public Optional<String> getFileFormatVersion() {
        return Optional.ofNullable(fileFormatVersion);
    }

    public boolean isYarn2Project() {
        return yarn2Project;
    }

    public List<YarnLockEntry> getEntries() {
        return entries;
    }
}
