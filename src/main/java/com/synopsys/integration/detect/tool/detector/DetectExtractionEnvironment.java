/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector;

import java.io.File;

import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

public class DetectExtractionEnvironment extends ExtractionEnvironment {
    private ExtractionId extractionId;

    public DetectExtractionEnvironment(final File outputDirectory, final ExtractionId extractionId) {
        super(outputDirectory);
        this.extractionId = extractionId;
    }

    public ExtractionId getExtractionId() {
        return extractionId;
    }
}
