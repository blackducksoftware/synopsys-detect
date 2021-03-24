/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public interface DetectableResult {
    boolean getPassed();

    String toDescription();

    List<Explanation> getExplanation();

    List<File> getRelevantFiles();
}
