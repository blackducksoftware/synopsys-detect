/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.Detector;

public class DetectorsPage {
    private final List<Detector> buildless;
    private final List<Detector> build;

    public DetectorsPage(final List<Detector> buildless, final List<Detector> build) {
        this.buildless = buildless;
        this.build = build;
    }

    public List<Detector> getBuildless() {
        return buildless;
    }

    public List<Detector> getBuild() {
        return build;
    }
}
