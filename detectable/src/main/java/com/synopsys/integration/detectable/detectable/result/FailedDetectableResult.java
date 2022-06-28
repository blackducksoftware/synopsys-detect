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
    public List<Explanation> getExplanation() { //TODO: (detectors): Only passed have these, why present on both? Should be called getPassedExplanations maybe?
        return Collections.emptyList();
    }

    @Override
    public List<File> getRelevantFiles() {
        return Collections.emptyList();
    }
}
