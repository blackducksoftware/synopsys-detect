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
package com.blackducksoftware.integration.hub.detect.workflow.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.log.IntLogger;

@Component
public class DetectSummaryManager {
    @Autowired
    private List<StatusSummaryProvider> statusSummaryProviders;

    public void logDetectResults(final IntLogger logger, final ExitCodeType exitCodeType) {
        final List<StatusSummary> statusSummaries = new ArrayList<>();
        for (final StatusSummaryProvider statusSummaryProvider : statusSummaryProviders) {
            statusSummaries.addAll(statusSummaryProvider.getStatusSummaries());
        }

        // sort by type, and within type, sort by description
        Collections.sort(statusSummaries, new Comparator<StatusSummary>() {
            @Override
            public int compare(final StatusSummary left, final StatusSummary right) {
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
        Class<? extends StatusSummary> previousSummaryClass = null;

        for (final StatusSummary statusSummary : statusSummaries) {
            if (previousSummaryClass != null && !previousSummaryClass.equals(statusSummary.getClass())) {
                logger.info("");
            }
            logger.info(String.format("%s: %s", statusSummary.getDescriptionKey(), statusSummary.getStatus().toString()));

            previousSummaryClass = statusSummary.getClass();
        }

        logger.info(String.format("Overall Status: %s", exitCodeType.toString()));
        logger.info("================================");
        logger.info("");
    }

}
