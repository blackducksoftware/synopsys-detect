/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip.model;

import java.util.List;

public class PipFreeze {
    private final List<PipFreezeEntry> entries;

    public PipFreeze(final List<PipFreezeEntry> entries) {
        this.entries = entries;
    }

    public List<PipFreezeEntry> getEntries() {
        return entries;
    }
}
