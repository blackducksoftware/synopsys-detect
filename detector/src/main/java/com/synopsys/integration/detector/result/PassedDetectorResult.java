package com.synopsys.integration.detector.result;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public class PassedDetectorResult extends DetectorResult {
    public PassedDetectorResult() {
        this("Passed.", null, Collections.emptyList(), Collections.emptyList());
    }

    public PassedDetectorResult(@NotNull String description) {
        this(description, null, Collections.emptyList(), Collections.emptyList());
    }

    public PassedDetectorResult(@NotNull String description, Class resultClass, List<Explanation> explanations, List<File> relevantFiles) {
        super(true, description, resultClass, explanations, relevantFiles);
    }
}
