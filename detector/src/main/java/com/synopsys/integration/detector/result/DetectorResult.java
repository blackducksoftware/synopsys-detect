/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public class DetectorResult {
    private final boolean passed;
    @NotNull
    private final String description;
    private final Class resultClass;
    private List<Explanation> explanations;
    private List<File> relevantFiles;

    public DetectorResult(final boolean passed, @NotNull final String description, final List<Explanation> explanations, List<File> relevantFiles) {
        this.passed = passed;
        this.description = description;
        this.explanations = explanations;
        this.resultClass = null;
        this.relevantFiles = relevantFiles;
    }

    public DetectorResult(final boolean passed, @NotNull final String description, final Class resultClass, final List<Explanation> explanations, List<File> relevantFiles) {
        this.passed = passed;
        this.description = description;
        this.resultClass = resultClass;
        this.explanations = explanations;
        this.relevantFiles = relevantFiles;
    }

    public boolean getPassed() {
        return passed;
    }

    public List<Explanation> getExplanations() {
        return explanations;
    }

    public List<File> getRelevantFiles() {
        return relevantFiles;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public Class getResultClass() {
        return resultClass;
    }
}
