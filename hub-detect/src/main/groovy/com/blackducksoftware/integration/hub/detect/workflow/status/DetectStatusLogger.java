package com.blackducksoftware.integration.hub.detect.workflow.status;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusLogger {
    public void logDetectResults(final IntLogger logger, List<Status> statusSummaries, final ExitCodeType exitCodeType) {
        // sort by type, and within type, sort by description
        Collections.sort(statusSummaries, new Comparator<Status>() {
            @Override
            public int compare(final Status left, final Status right) {
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
        Class<? extends Status> previousSummaryClass = null;

        for (final Status status : statusSummaries) {
            if (previousSummaryClass != null && !previousSummaryClass.equals(status.getClass())) {
                logger.info("");
            }
            logger.info(String.format("%s: %s", status.getDescriptionKey(), status.getStatusType().toString()));

            previousSummaryClass = status.getClass();
        }

        logger.info(String.format("Overall Status: %s", exitCodeType.toString()));
        logger.info("================================");
        logger.info("");
    }
}
