package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
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
    private String scanMode;

    public RapidModeLogReportOperation(ExitCodePublisher exitCodePublisher, RapidScanResultAggregator rapidScanResultAggregator, BlackduckScanMode mode) {
        this.exitCodePublisher = exitCodePublisher;
        this.rapidScanResultAggregator = rapidScanResultAggregator;
        this.scanMode = mode.displayName();
    }

    public RapidScanResultSummary perform(List<DeveloperScansScanView> results) throws DetectUserFriendlyException {
         RapidScanAggregateResult aggregateResult = rapidScanResultAggregator.aggregateData(results);
        logger.info(String.format("%s:", scanMode + RapidScanDetectResult.NONPERSISTENT_SCAN_RESULT_DETAILS_HEADING));
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
