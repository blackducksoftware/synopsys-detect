package com.synopsys.integration.detect.workflow.blackduck.developer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

public class RapidScanResultAggregatorTest {
    @Test
    public void testEmptyResults() {
        List<DeveloperScansScanView> results = Collections.emptyList();
        RapidScanResultAggregator aggregator = new RapidScanResultAggregator();
        RapidScanAggregateResult aggregateResult = aggregator.aggregateData(results);
        BufferedIntLogger logger = new BufferedIntLogger();
        aggregateResult.logResult(logger);
        RapidScanResultSummary summary = aggregateResult.getSummary();
        assertEquals(0, summary.getPolicyErrorCount());
        assertEquals(0, summary.getPolicyWarningCount());
        assertEquals(0, summary.getSecurityErrorCount());
        assertEquals(0, summary.getSecurityWarningCount());
        assertEquals(0, summary.getLicenseErrorCount());
        assertEquals(0, summary.getLicenseWarningCount());
        assertFalse(logger.getOutputList(LogLevel.INFO).isEmpty());
    }

    @Test
    public void testResults() {
        List<DeveloperScansScanView> results = createResultList();
        RapidScanResultAggregator aggregator = new RapidScanResultAggregator();
        RapidScanAggregateResult aggregateResult = aggregator.aggregateData(results);
        BufferedIntLogger logger = new BufferedIntLogger();
        aggregateResult.logResult(logger);
        RapidScanResultSummary summary = aggregateResult.getSummary();
        assertEquals(1, summary.getPolicyErrorCount());
        // TODO this is based on the number of warning messages, still not sure what consitutes 
        // a warning over an error.
        //assertEquals(1, summary.getPolicyWarningCount());
        assertEquals(1, summary.getSecurityErrorCount());
        // TODO this is based on the number of warning messages, still not sure what consitutes 
        // a warning over an error.
        //assertEquals(1, summary.getSecurityWarningCount());
        assertEquals(1, summary.getLicenseErrorCount());
        // TODO this is based on the number of warning messages, still not sure what consitutes 
        // a warning over an error.
        //assertEquals(1, summary.getLicenseWarningCount());
        assertFalse(logger.getOutputList(LogLevel.INFO).isEmpty());
    }

    private List<DeveloperScansScanView> createResultList() {
        List<DeveloperScansScanView> resultList = new ArrayList<>();
        DeveloperScansScanView view = createView();
        resultList.add(view);
        return resultList;
    }

    private DeveloperScansScanView createView() {
        return new DeveloperScansScanView() {
            @Override
            public String getComponentName() {
                return "component_1";
            }

            @Override
            public String getVersionName() {
                return "component_version_1";
            }

            @Override
            public String getComponentIdentifier() {
                return "component_1:component_version_1";
            }

            @Override
            public List<DeveloperScansScanItemsViolatingPoliciesView> getViolatingPolicies() {
                List<DeveloperScansScanItemsViolatingPoliciesView> violatingPolicies = new ArrayList<>();

                DeveloperScansScanItemsViolatingPoliciesView view = new DeveloperScansScanItemsViolatingPoliciesView();
                view.setPolicyName("component_policy");
                view.setPolicyName("vulnerability_policy");
                view.setPolicyName("license_policy");

                violatingPolicies.add(view);
                return violatingPolicies;
            }

            @Override
            public List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> getPolicyViolationVulnerabilities() {
                List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesView> vulnerabilities = new ArrayList<>();
                DeveloperScansScanItemsPolicyViolationVulnerabilitiesView view = new DeveloperScansScanItemsPolicyViolationVulnerabilitiesView() {
                    @Override
                    public String getName() {
                        return "Vulnerability violation";
                    }

                    @Override
                    public String getDescription() {
                        return "Violation Description";
                    }

                    @Override
                    public List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> getViolatingPolicies() {
                        List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> violatingPolicies = new ArrayList<>();
                        
                        DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView view = new DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView();
                        view.setPolicyName("vulnerability_policy");
                 
                        violatingPolicies.add(view);
                        return violatingPolicies;
                    }
                };
                vulnerabilities.add(view);
                return vulnerabilities;
            }

            @Override
            public List<DeveloperScansScanItemsPolicyViolationLicensesView> getPolicyViolationLicenses() {
                List<DeveloperScansScanItemsPolicyViolationLicensesView> licenses = new ArrayList<>();
                
                DeveloperScansScanItemsPolicyViolationLicensesView view = new DeveloperScansScanItemsPolicyViolationLicensesView() {
                    @Override
                    public String getName() {
                        return "License name";
                    }

                    @Override
                    public List<DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView> getViolatingPolicies() {
                        List<DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView> violatingPolicies = new ArrayList<>();
                        
                        DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView view = new DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView(); 
                        view.setPolicyName("license_policy");
                        
                        violatingPolicies.add(view);
                        return violatingPolicies;
                    }
                };
                licenses.add(view);
                return licenses;
            }
        };
    }

}
