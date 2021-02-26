/*
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

public class PassedDetectableResult implements DetectableResult {
    private final List<Explanation> explanations;
    private final List<File> relevantFiles;

    public PassedDetectableResult(final List<Explanation> explanations, List<File> relevantFiles) {
        this.explanations = explanations;
        this.relevantFiles = relevantFiles;
    }

    public PassedDetectableResult(final List<Explanation> explanations) {
        this.explanations = explanations;
        this.relevantFiles = Collections.emptyList();
    }

    public PassedDetectableResult() {
        this.explanations = Collections.emptyList();
        this.relevantFiles = Collections.emptyList();
    }

    public PassedDetectableResult(Explanation explanation) {
        this.explanations = Collections.singletonList(explanation);
        this.relevantFiles = Collections.emptyList();
    }

    @Override
    public boolean getPassed() {
        return true;
    }

    @Override
    public String toDescription() {
        return "Passed.";
    }

    @Override
    public List<Explanation> getExplanation() {
        return explanations;
    }

    @Override
    public List<File> getRelevantFiles() {
        return relevantFiles;
    }
}
