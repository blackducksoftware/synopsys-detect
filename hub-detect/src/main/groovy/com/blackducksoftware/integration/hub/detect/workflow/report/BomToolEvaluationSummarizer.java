/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public abstract class BomToolEvaluationSummarizer {
    protected Map<File, List<BomToolEvaluation>> groupByDirectory(final List<BomToolEvaluation> results) {
        return results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));
    }

    protected int filesystemCompare(final String left, final String right) {
        final String[] pieces1 = left.split(Pattern.quote(File.separator));
        final String[] pieces2 = right.split(Pattern.quote(File.separator));
        final int min = Math.min(pieces1.length, pieces2.length);
        for (int i = 0; i < min; i++) {
            final int compared = pieces1[i].compareTo(pieces2[i]);
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(pieces1.length, pieces2.length);
    }
}
