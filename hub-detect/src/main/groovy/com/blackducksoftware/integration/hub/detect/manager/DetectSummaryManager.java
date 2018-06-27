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
package com.blackducksoftware.integration.hub.detect.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.SummaryResult;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.SummaryResultProvider;
import com.blackducksoftware.integration.hub.detect.util.DetectLoggingUtils;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectSummaryManager {
    @Autowired
    private List<SummaryResultProvider> summaryResultProviders;

    public void logResults(final IntLogger logger, final ExitCodeType exitCodeType) {
        final List<SummaryResult> detectSummaryResults = new ArrayList<>();
        for (final SummaryResultProvider summaryResultProvider : summaryResultProviders) {
            detectSummaryResults.addAll(summaryResultProvider.getDetectSummaryResults());
        }

        // sort by type, and within type, sort by description
        Collections.sort(detectSummaryResults, new Comparator<SummaryResult>() {
            @Override
            public int compare(final SummaryResult left, final SummaryResult right) {
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
        Class<? extends SummaryResult> previousResultClass = null;
        for (final SummaryResult detectSummaryResult : detectSummaryResults) {
            if (previousResultClass != null && !previousResultClass.equals(detectSummaryResult.getClass())) {
                logger.info("");
            }
            final LogLevel detectSummaryLogLevel = detectSummaryResult.getLogLevel();
            final String detectSummaryResultString = String.format("%s: %s", detectSummaryResult.getDescriptionKey(), detectSummaryResult.getStatus().toString());

            DetectLoggingUtils.logAtLevel(logger, detectSummaryLogLevel, detectSummaryResultString);

            previousResultClass = detectSummaryResult.getClass();
        }

        logger.info(String.format("Overall Status: %s", exitCodeType.toString()));
        logger.info("================================");
        logger.info("");
    }

}
