/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.pages;

public class IndexPage {
    private final String version;

    public IndexPage(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
