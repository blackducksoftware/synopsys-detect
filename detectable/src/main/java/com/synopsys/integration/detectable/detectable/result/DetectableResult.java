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
