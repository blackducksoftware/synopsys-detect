/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.extraction;

import com.synopsys.integration.detector.base.DetectorType;

public class ExtractionId {
    private final Integer id;
    private final String extractionType;

    public ExtractionId(final DetectorType detectorType, final Integer id) {
        extractionType = detectorType.toString();
        this.id = id;
    }

    public ExtractionId(final String extractionType, final Integer id) {
        this.id = id;
        this.extractionType = extractionType;
    }

    public String toUniqueString() {
        return extractionType + "-" + id;
    }

    public Integer getId() {
        return id;
    }
}
