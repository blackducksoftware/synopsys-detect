/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public class FailedDetectableResult implements DetectableResult {
    @Override
    public boolean getPassed() {
        return false;
    }

    @Override
    public String toDescription() {
        return "Failed.";
    }

    @Override
    public List<Explanation> getExplanation() {
        return Collections.emptyList();
    }

    @Override
    public List<File> getRelevantFiles() {
        return Collections.emptyList();
    }
}
