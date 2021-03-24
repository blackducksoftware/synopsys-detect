/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateStepReplaceInEach implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    final String targetPattern;
    final String replacementString;

    public IntermediateStepReplaceInEach(final String targetPattern, final String replacementString) {
        this.targetPattern = targetPattern;
        this.replacementString = replacementString;
    }

    @Override
    public List<String> process(final List<String> input) {
        final List<String> results = new ArrayList<>();
        logger.trace(String.format("Replace target pattern: %s; replacement string: %s", targetPattern, replacementString));
        for (final String inputItem : input) {
            final String modifiedInputItem = inputItem.replaceAll(targetPattern, replacementString);
            logger.trace(String.format("Edit changed %s to %s", inputItem, modifiedInputItem));
            results.add(modifiedInputItem);
        }
        return results;
    }
}
