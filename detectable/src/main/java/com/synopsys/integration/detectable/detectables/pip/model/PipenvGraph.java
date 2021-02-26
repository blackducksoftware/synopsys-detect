/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip.model;

import java.util.List;

public class PipenvGraph {
    private final List<PipenvGraphEntry> entries;

    public PipenvGraph(final List<PipenvGraphEntry> entries) {
        this.entries = entries;
    }

    public List<PipenvGraphEntry> getEntries() {
        return entries;
    }
}
