package com.synopsys.integration.detect.workflow.blackduck.developer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;

public class RapidModeLogReportOperationTest {

    @Test
    void testPublishesPolicyViolation() throws DetectUserFriendlyException {
        ExitCodePublisher exitCodePublisher = Mockito.mock(ExitCodePublisher.class);
        RapidScanResultAggregator rapidScanResultAggregator = Mockito.mock(RapidScanResultAggregator.class);
        RapidModeLogReportOperation op = new RapidModeLogReportOperation(exitCodePublisher, rapidScanResultAggregator, BlackduckScanMode.RAPID);

        List<DeveloperScansScanView> results = new LinkedList<>();
        DeveloperScansScanView resultView = Mockito.mock(DeveloperScansScanView.class);
        results.add(resultView);
        RapidScanAggregateResult aggregateResult = Mockito.mock(RapidScanAggregateResult.class);
        Mockito.when(rapidScanResultAggregator.aggregateData(results)).thenReturn(aggregateResult);

        RapidScanResultSummary summary = Mockito.mock(RapidScanResultSummary.class);
        Mockito.when(summary.hasErrors()).thenReturn(true);
        Mockito.when(aggregateResult.getSummary()).thenReturn(summary);

        Set<String> policyViolationNames = new HashSet<>();
        policyViolationNames.add("testPolicy1");
        policyViolationNames.add("testPolicy2");
        Mockito.when(summary.getPolicyViolationNames()).thenReturn(policyViolationNames);

        RapidScanResultSummary returnedSummary = op.perform(results);

        assertEquals(summary, returnedSummary);
        Mockito.verify(exitCodePublisher, Mockito.times(1))
            .publishExitCode(Mockito.eq(ExitCodeType.FAILURE_POLICY_VIOLATION), org.mockito.AdditionalMatchers.find(".* 2.*violation.*"));
    }
}
