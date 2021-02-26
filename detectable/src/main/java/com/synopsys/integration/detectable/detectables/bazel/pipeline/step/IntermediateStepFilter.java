/*
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

public class IntermediateStepFilter implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String regex;

    public IntermediateStepFilter(final String regex) {
        this.regex = regex;
    }

    @Override
    public List<String> process(final List<String> input) {
        final List<String> output = new ArrayList<>();
        logger.trace(String.format("Filtering with regex %s", regex));
        for (final String inputItem : input) {
            if (inputItem.matches(regex)) {
                logger.trace(String.format("Filter keeping: %s", inputItem));
                output.add(inputItem);
            }
        }
        return output;
    }
}
