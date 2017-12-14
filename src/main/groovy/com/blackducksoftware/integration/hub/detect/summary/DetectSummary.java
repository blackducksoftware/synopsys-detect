/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.log.IntLogger;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectSummary {
    @Autowired
    private List<SummaryResultReporter> summaryResultReporters;

    public void logResults(final IntLogger logger, final ExitCodeType exitCodeType) {
        final List<DetectSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final SummaryResultReporter summaryResultReporter : summaryResultReporters) {
            detectSummaryResults.addAll(summaryResultReporter.getDetectSummaryResults());
        }

        // sort by type, and within type, sort by description
        Collections.sort(detectSummaryResults, new Comparator<DetectSummaryResult>() {
            @Override
            public int compare(final DetectSummaryResult left, final DetectSummaryResult right) {
                if (left.getClass() == right.getClass()) {
                    return left.getDescriptionKey().compareTo(right.getDescriptionKey());
                } else {
                    return left.getClass().getName().compareTo(right.getClass().getName());
                }
            }
        });
        logger.info("");
        logger.info("");
        logger.info("======== Detect Results ========");
        Class<? extends DetectSummaryResult> previousResultClass = null;
        for (final DetectSummaryResult detectSummaryResult : detectSummaryResults) {
            if (previousResultClass != null && !previousResultClass.equals(detectSummaryResult.getClass())) {
                logger.info("");
            }
            logger.info(String.format("%s: %s", detectSummaryResult.getDescriptionKey(), detectSummaryResult.getResult().toString()));
            previousResultClass = detectSummaryResult.getClass();
        }

        logger.info(String.format("Overall Status: %s", exitCodeType.toString()));
        logger.info("================================");
        logger.info("");
    }

}
