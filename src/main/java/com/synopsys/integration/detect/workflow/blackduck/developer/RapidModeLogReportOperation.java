/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.log.Slf4jIntLogger;

public class RapidModeLogReportOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExitCodePublisher exitCodePublisher;
    private final RapidScanResultAggregator rapidScanResultAggregator;

    public RapidModeLogReportOperation(final ExitCodePublisher exitCodePublisher, final RapidScanResultAggregator rapidScanResultAggregator) {
        this.exitCodePublisher = exitCodePublisher;
        this.rapidScanResultAggregator = rapidScanResultAggregator;
    }

    public RapidScanResultSummary perform(List<DeveloperScanComponentResultView> results) throws DetectUserFriendlyException {
        RapidScanAggregateResult aggregateResult = rapidScanResultAggregator.aggregateData(results);
        logger.info(String.format("%s:", RapidScanDetectResult.RAPID_SCAN_RESULT_DETAILS_HEADING));
        aggregateResult.logResult(new Slf4jIntLogger(logger));
        RapidScanResultSummary summary = aggregateResult.getSummary();
        if (summary.hasErrors()) {
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_POLICY_VIOLATION, createViolationMessage(summary.getPolicyViolationNames()));
        }
        return summary;
    }

    private String createViolationMessage(Set<String> violatedPolicyNames) {
        StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append("Black Duck found:");
        stringBuilder.append(fixComponentPlural(" %d %s in violation", violatedPolicyNames.size()));
        return stringBuilder.toString();
    }

    private String fixComponentPlural(String formatString, int count) {
        String label = "components";
        if (count == 1)
            label = "component";
        return String.format(formatString, count, label);
    }
}
