package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateStepParseReplaceInEachLine implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    final String targetPattern;
    final String replacementString;

    public IntermediateStepParseReplaceInEachLine(String targetPattern, String replacementString) {
        this.targetPattern = targetPattern;
        this.replacementString = replacementString;
    }

    @Override
    public List<String> process(List<String> input) {
        List<String> results = new ArrayList<>();
        logger.trace("Replace target pattern: {}; replacement string: {}", targetPattern, replacementString);
        for (String inputItem : input) {
            String modifiedInputItem = inputItem.replaceAll(targetPattern, replacementString);
            logger.trace("Edit changed {} to {}", inputItem, modifiedInputItem);
            results.add(modifiedInputItem);
        }
        return results;
    }
}
