/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.status;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusLogger {

    public void logDetectStatus(IntLogger logger, List<Status> statusSummaries, List<DetectResult> detectResults, List<DetectIssue> detectIssues, ExitCodeType exitCodeType) {
        logger.info("");
        logger.info("");

        logDetectIssues(logger, detectIssues);
        logDetectResults(logger, detectResults);
        logDetectStatus(logger, statusSummaries);

        logger.info(String.format("Overall Status: %s - %s", exitCodeType.toString(), exitCodeType.getDescription()));
        logger.info("");
        logger.info("===============================");
        logger.info("");
    }

    private void logDetectIssues(IntLogger logger, List<DetectIssue> detectIssues) {
        if (!detectIssues.isEmpty()) {
            logger.info("======== Detect Issues ========");
            logger.info("");

            Predicate<DetectIssue> detectorsFilter = issue -> issue.getType() == DetectIssueType.DETECTOR;
            Predicate<DetectIssue> exceptionsFilter = issue -> issue.getType() == DetectIssueType.EXCEPTION;
            Predicate<DetectIssue> deprecationsFilter = issue -> issue.getType() == DetectIssueType.DEPRECATION;
            logIssuesInGroup(logger, "DETECTORS:", detectorsFilter, detectIssues);
            logIssuesInGroup(logger, "EXCEPTIONS:", exceptionsFilter, detectIssues);
            logIssuesInGroup(logger, "DEPRECATIONS:", deprecationsFilter, detectIssues);
        }
    }

    private void logIssuesInGroup(IntLogger logger, String groupHeading, Predicate<DetectIssue> issueFilter, List<DetectIssue> detectIssues) {
        List<DetectIssue> detectors = detectIssues.stream().filter(issueFilter).collect(Collectors.toList());
        if (!detectors.isEmpty()) {
            logger.info(groupHeading);
            detectors.stream().flatMap(issue -> issue.getMessages().stream()).forEach(line -> logger.info("\t" + line));
            logger.info("");
        }
    }

    private void logDetectResults(IntLogger logger, List<DetectResult> detectResults) {
        if (!detectResults.isEmpty()) {
            logger.info("======== Detect Result ========");
            logger.info("");
            for (DetectResult detectResult : detectResults) {
                logger.info(detectResult.getResultMessage());
            }
            logger.info("");
        }
    }

    private void logDetectStatus(IntLogger logger, List<Status> statusSummaries) {
        // sort by type, and within type, sort by description
        statusSummaries.sort((left, right) -> {
            if (left.getClass() == right.getClass()) {
                return left.getDescriptionKey().compareTo(right.getDescriptionKey());
            } else {
                return left.getClass().getName().compareTo(right.getClass().getName());
            }
        });
        logger.info("======== Detect Status ========");
        logger.info("");
        Class<? extends Status> previousSummaryClass = null;

        for (Status status : statusSummaries) {
            if (previousSummaryClass != null && !previousSummaryClass.equals(status.getClass())) {
                logger.info("");
            }
            logger.info(String.format("%s: %s", status.getDescriptionKey(), status.getStatusType().toString()));

            previousSummaryClass = status.getClass();
        }
    }
}
