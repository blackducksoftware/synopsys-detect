/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateStepSplitEach implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String regex;

    public IntermediateStepSplitEach(final String regex) {
        this.regex = regex;
    }

    @Override
    public List<String> process(final List<String> input) {
        final List<String> results = new ArrayList<>();
        for (final String inputItem : input) {
            final String[] splitLines = inputItem.split(regex);
            results.addAll(Arrays.asList(splitLines));
        }
        logger.trace(String.format("SplitEach returning %d lines", results.size()));
        return results;
    }
}
