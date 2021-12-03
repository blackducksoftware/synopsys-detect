package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateStepSplitEach implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String regex;

    public IntermediateStepSplitEach(String regex) {
        this.regex = regex;
    }

    @Override
    public List<String> process(List<String> input) {
        List<String> results = new ArrayList<>();
        for (String inputItem : input) {
            String[] splitLines = inputItem.split(regex);
            results.addAll(Arrays.asList(splitLines));
        }
        logger.trace(String.format("SplitEach returning %d lines", results.size()));
        return results;
    }
}
