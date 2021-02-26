/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;

public class YarnLock {
    private final List<YarnLockEntry> entries;

    public YarnLock(List<YarnLockEntry> entries) {
        this.entries = entries;
    }

    public List<YarnLockEntry> getEntries() {
        return entries;
    }
}
