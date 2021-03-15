/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusLogger {

    public void logDetectStatus(IntLogger logger, List<Status> statusSummaries, List<DetectResult> detectResults, List<DetectIssue> detectIssues, List<Operation> detectOperations, ExitCodeType exitCodeType) {
        logger.info("");
        logger.info("");

        logDetectIssues(logger, detectIssues);
        logDetectResults(logger, detectResults);
        logDetectStatus(logger, statusSummaries);

        logger.info(String.format("Overall Status: %s - %s", exitCodeType.toString(), exitCodeType.getDescription()));
        logger.info("");
        logger.info("===============================");
        logger.info("");
        logger.debug("=== Additional Information ===");
        logDetectOperations(logger, detectOperations);

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
        List<DetectIssue> detectIssueList = detectIssues.stream().filter(issueFilter).collect(Collectors.toList());
        if (!detectIssueList.isEmpty()) {
            logger.info(groupHeading);
            for (DetectIssue issue : detectIssueList) {
                logger.info("\t" + issue.getTitle());
                issue.getMessages().forEach(message -> logger.info("\t\t" + message));
                logger.info("");
            }
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

    private void logDetectOperations(IntLogger logger, List<Operation> operations) {
        List<Operation> sortedOperations = operations.stream()
                                               .sorted(Comparator.comparing(Operation::getExecutionTime)
                                                           .thenComparing(Operation::getDescriptionKey))
                                               .collect(Collectors.toList());
        logger.debug("====== Detect Operations ======");

        for (Operation operation : sortedOperations) {
            logger.debug("");
            logger.debug(String.format("%s: %s (%s)", operation.getDescriptionKey(), operation.getStatusType().toString(), Operation.formatExecutionTime(operation.getExecutionTime())));
        }
        logger.debug("");
        logger.debug("===============================");
        logger.debug("");
    }
}
