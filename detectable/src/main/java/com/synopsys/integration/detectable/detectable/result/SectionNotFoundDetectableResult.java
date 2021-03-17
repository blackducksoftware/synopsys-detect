/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class SectionNotFoundDetectableResult extends FailedDetectableResult {
    private final String fileName;
    private final String missingSection;

    public SectionNotFoundDetectableResult(final String fileName, final String missingSection) {
        this.fileName = fileName;
        this.missingSection = missingSection;
    }

    @Override
    public String toDescription() {
        return String.format("%s was not found in %s.", missingSection, fileName);
    }

}
