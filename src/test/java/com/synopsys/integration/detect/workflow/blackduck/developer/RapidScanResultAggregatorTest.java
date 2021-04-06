package com.synopsys.integration.detect.workflow.blackduck.developer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationLicenseView;
import com.synopsys.integration.blackduck.api.manual.view.PolicyViolationVulnerabilityView;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanAggregateResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

public class RapidScanResultAggregatorTest {
    @Test
    public void testEmptyResults() {
        List<DeveloperScanComponentResultView> results = Collections.emptyList();
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
        List<DeveloperScanComponentResultView> results = createResultList();
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

    private List<DeveloperScanComponentResultView> createResultList() {
        List<DeveloperScanComponentResultView> resultList = new ArrayList<>();
        DeveloperScanComponentResultView view = createView();
        resultList.add(view);
        return resultList;
    }

    private DeveloperScanComponentResultView createView() {
        return new DeveloperScanComponentResultView() {
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
            public Set<String> getViolatingPolicyNames() {
                Set<String> policyNames = new HashSet<>();
                policyNames.add("component_policy");
                policyNames.add("vulnerability_policy");
                policyNames.add("license_policy");
                return policyNames;
            }

            @Override
            public Set<PolicyViolationVulnerabilityView> getPolicyViolationVulnerabilities() {
                Set<PolicyViolationVulnerabilityView> vulnerabilities = new HashSet<>();
                PolicyViolationVulnerabilityView view = new PolicyViolationVulnerabilityView() {
                    @Override
                    public String getName() {
                        return "Vulnerability violation";
                    }

                    @Override
                    public String getDescription() {
                        return "Violation Description";
                    }

                    @Override
                    public Set<String> getViolatingPolicyNames() {
                        return Collections.singleton("vulnerability_policy");
                    }

                    @Override
                    public String getErrorMessage() {
                        return "vulnerability_error_1";
                    }

                    @Override
                    public String getWarningMessage() {
                        return "vulnerability_warning_1";
                    }
                };
                vulnerabilities.add(view);
                return vulnerabilities;
            }

            @Override
            public Set<PolicyViolationLicenseView> getPolicyViolationLicenses() {
                Set<PolicyViolationLicenseView> licenses = new HashSet<>();
                PolicyViolationLicenseView view = new PolicyViolationLicenseView() {
                    @Override
                    public String getLicenseName() {
                        return "License name";
                    }

                    @Override
                    public Set<String> getViolatingPolicyNames() {
                        return Collections.singleton("license_policy");
                    }

                    @Override
                    public String getErrorMessage() {
                        return "license_error_1";
                    }

                    @Override
                    public String getWarningMessage() {
                        return "license_warning_1";
                    }
                };
                licenses.add(view);
                return licenses;
            }

            @Override
            public String getErrorMessage() {
                return "component_1_error_message";
            }

            @Override
            public String getWarningMessage() {
                return "component_1_warning_message";
            }
        };
    }

}
