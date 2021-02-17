/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
