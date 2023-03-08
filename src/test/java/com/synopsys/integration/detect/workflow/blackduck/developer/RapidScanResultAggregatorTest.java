package com.synopsys.integration.detect.workflow.blackduck.developer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsComponentViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsComponentViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.api.generated.view.ScanFullResultView;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

public class RapidScanResultAggregatorTest {
    @Test
    public void testEmptyResults() {
        List<ScanFullResultView> results = Collections.emptyList();
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
        List<ScanFullResultView> results = createResultList();
        RapidScanResultAggregator aggregator = new RapidScanResultAggregator();
        RapidScanAggregateResult aggregateResult = aggregator.aggregateData(results);
        BufferedIntLogger logger = new BufferedIntLogger();
        aggregateResult.logResult(logger);
        RapidScanResultSummary summary = aggregateResult.getSummary();
        assertEquals(1, summary.getPolicyErrorCount());
        assertEquals(1, summary.getPolicyWarningCount());
        assertEquals(1, summary.getSecurityErrorCount());
        assertEquals(1, summary.getSecurityWarningCount());
        assertEquals(1, summary.getLicenseErrorCount());
        assertEquals(1, summary.getLicenseWarningCount());
        assertFalse(logger.getOutputList(LogLevel.INFO).isEmpty());
    }

    private List<ScanFullResultView> createResultList() {
        List<ScanFullResultView> resultList = new ArrayList<>();
        ScanFullResultView view = createView();
        resultList.add(view);
        return resultList;
    }

    private ScanFullResultView createView() {
        return new ScanFullResultView() {
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
            public List<ScanFullResultItemsComponentViolatingPoliciesView> getComponentViolatingPolicies() {
                List<ScanFullResultItemsComponentViolatingPoliciesView> componentViolatingPolicies = new ArrayList<>();
                
                ScanFullResultItemsComponentViolatingPoliciesView componentViolatingPolicy = new ScanFullResultItemsComponentViolatingPoliciesView();
                componentViolatingPolicy.setPolicyName("component_policy");
                componentViolatingPolicy.setPolicySeverity("CRITICAL");
                
                ScanFullResultItemsComponentViolatingPoliciesView componentViolatingPolicy2 = new ScanFullResultItemsComponentViolatingPoliciesView();
                componentViolatingPolicy2.setPolicyName("component_policy_warning");
                componentViolatingPolicy2.setPolicySeverity("MINOR");
                
                componentViolatingPolicies.add(componentViolatingPolicy);
                componentViolatingPolicies.add(componentViolatingPolicy2);
                return componentViolatingPolicies;
            }

            @Override
            public List<ScanFullResultItemsViolatingPoliciesView> getViolatingPolicies() {
                List<ScanFullResultItemsViolatingPoliciesView> violatingPolicies = new ArrayList<>();

                ScanFullResultItemsViolatingPoliciesView view = new ScanFullResultItemsViolatingPoliciesView();
                view.setPolicyName("component_policy");
                view.setPolicySeverity("CRITICAL");
                ScanFullResultItemsViolatingPoliciesView view2 = new ScanFullResultItemsViolatingPoliciesView();
                view2.setPolicyName("vulnerability_policy");
                view2.setPolicySeverity("CRITICAL");
                ScanFullResultItemsViolatingPoliciesView view3 = new ScanFullResultItemsViolatingPoliciesView();
                view3.setPolicyName("license_policy");
                view3.setPolicySeverity("MINOR");

                violatingPolicies.add(view);
                violatingPolicies.add(view2);
                violatingPolicies.add(view3);
                return violatingPolicies;
            }

            @Override
            public List<ScanFullResultItemsPolicyViolationVulnerabilitiesView> getPolicyViolationVulnerabilities() {
                List<ScanFullResultItemsPolicyViolationVulnerabilitiesView> vulnerabilities = new ArrayList<>();
                ScanFullResultItemsPolicyViolationVulnerabilitiesView view = new ScanFullResultItemsPolicyViolationVulnerabilitiesView() {
                    @Override
                    public String getName() {
                        return "Vulnerability violation";
                    }

                    @Override
                    public String getDescription() {
                        return "Violation Description";
                    }

                    @Override
                    public List<ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> getViolatingPolicies() {
                        List<ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> violatingPolicies = new ArrayList<>();
                        
                        ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView view = new ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView();
                        view.setPolicyName("vulnerability_policy");
                        view.setPolicySeverity("CRITICAL");
                        
                        ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView view2 = new ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView();
                        view2.setPolicyName("vulnerability_policy_warning");
                        view2.setPolicySeverity("MINOR");
                 
                        violatingPolicies.add(view);
                        violatingPolicies.add(view2);
                        return violatingPolicies;
                    }
                };
                vulnerabilities.add(view);
                return vulnerabilities;
            }

            @Override
            public List<ScanFullResultItemsPolicyViolationLicensesView> getPolicyViolationLicenses() {
                List<ScanFullResultItemsPolicyViolationLicensesView> licenses = new ArrayList<>();
                
                ScanFullResultItemsPolicyViolationLicensesView view = new ScanFullResultItemsPolicyViolationLicensesView() {
                    @Override
                    public String getName() {
                        return "License name";
                    }

                    @Override
                    public List<ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView> getViolatingPolicies() {
                        List<ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView> violatingPolicies = new ArrayList<>();
                        
                        ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView view = new ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView(); 
                        view.setPolicyName("license_policy");
                        view.setPolicySeverity("CRITICAL");
                        
                        ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView view2 = new ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView(); 
                        view2.setPolicyName("license_policy_warning");
                        view2.setPolicySeverity("MINOR");
                        
                        violatingPolicies.add(view);
                        violatingPolicies.add(view2);
                        return violatingPolicies;
                    }
                };
                licenses.add(view);
                return licenses;
            }
        };
    }

}
