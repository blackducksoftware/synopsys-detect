package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.DeveloperScansScanItemsViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;

public class RapidScanComponentGroupDetail {
    private final RapidScanDetailGroup group;
    private final Set<String> errorMessages = new LinkedHashSet<>();
    private final Set<String> warningMessages = new LinkedHashSet<>();
    private final Set<String> policyNames = new LinkedHashSet<>();

    public RapidScanComponentGroupDetail(RapidScanDetailGroup group) {
        this.group = group;
    }

    public String getGroupName() {
        return group.getDisplayName();
    }

    public RapidScanDetailGroup getGroup() {
        return group;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    public Set<String> getWarningMessages() {
        return warningMessages;
    }

    public Set<String> getPolicyNames() {
        return policyNames;
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    public boolean hasWarnings() {
        return !warningMessages.isEmpty();
    }

    public void addError(String errorMessage) {
        errorMessages.add(errorMessage);
    }

    public void addErrors(Set<String> errorMessages) {
        this.errorMessages.addAll(errorMessages);
    }

    public void addWarning(String warningMessage) {
        warningMessages.add(warningMessage);
    }

    public void addWarnings(Set<String> warningMessages) {
        this.warningMessages.addAll(warningMessages);
    }

    public void addPolicies(Set<String> policyNames) {
        this.policyNames.addAll(policyNames);
    }

    public void addMessages(String errorMessage, String warningMessage) {
        if (StringUtils.isNotBlank(errorMessage)) {
            addError(errorMessage);
        }
        if (StringUtils.isNotBlank(warningMessage)) {
            addWarning(warningMessage);
        }
    }

    public void addComponentMessages(DeveloperScansScanView resultView) {
        String baseMessage = getBaseMessage(resultView);

        List<DeveloperScansScanItemsViolatingPoliciesView> violatingPolicies = resultView.getViolatingPolicies();

        String errorMessage = "", warningMessage = "";

        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsViolatingPoliciesView violation = violatingPolicies.get(i);

            if (violation.getPolicySeverity().equals("CRITICAL") || violation.getPolicySeverity().equals("BLOCKER")) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += ", ";
                }
                
                errorMessage += violation.getPolicyName();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += ", ";
                }
                
                warningMessage += violation.getPolicyName();
            }
        }
        addMessages(errorMessage, warningMessage);
    }

    public void addLicenseMessages(DeveloperScansScanView resultView, DeveloperScansScanItemsPolicyViolationLicensesView licensePolicyViolation) {
        String baseMessage = getBaseMessage(resultView);
        
        List<DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView> violatingPolicies = licensePolicyViolation.getViolatingPolicies();
        
        String errorMessage = "", warningMessage = "";
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsPolicyViolationLicensesViolatingPoliciesView violation = violatingPolicies.get(i);
                    
            if (violation.getPolicySeverity().equals("CRITICAL") || violation.getPolicySeverity().equals("BLOCKER")) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += ", ";
                }
                
                errorMessage += violation.getPolicyName() + ": license " + licensePolicyViolation.getName();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += ", ";
                }
                
                warningMessage += violation.getPolicyName() + ": license " + licensePolicyViolation.getName();
            }
        }
        addMessages(errorMessage, warningMessage);
    }
    
    public void addVulnerabilityMessages(DeveloperScansScanView resultView,
            DeveloperScansScanItemsPolicyViolationVulnerabilitiesView vulnerability) {
        String baseMessage = getBaseMessage(resultView);
        
        List<DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> violatingPolicies = vulnerability.getViolatingPolicies();
        
        String errorMessage = "", warningMessage = "";
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            DeveloperScansScanItemsPolicyViolationVulnerabilitiesViolatingPoliciesView violation = violatingPolicies.get(i);
            
            if (violation.getPolicySeverity().equals("CRITICAL") || violation.getPolicySeverity().equals("BLOCKER")) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += ", ";
                }
                
                errorMessage += violation.getPolicyName();
                errorMessage += ": found vulnerability " + vulnerability.getName();
                errorMessage += " with severity " + vulnerability.getVulnSeverity();
                errorMessage += " and CVSS score " + vulnerability.getOverallScore();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += ", ";
                }
                
                warningMessage += violation.getPolicyName();
                warningMessage += ": found vulnerability " + vulnerability.getName();
                warningMessage += " with severity " + vulnerability.getVulnSeverity();
                warningMessage += " and CVSS score " + vulnerability.getOverallScore();
            }
        }
        addMessages(errorMessage, warningMessage);
    }
    
    private String getBaseMessage(DeveloperScansScanView resultView) {
        String baseMessage = "Component " + resultView.getComponentName() + " version " + resultView.getVersionName();
        if (StringUtils.isNotBlank(resultView.getExternalId())) {
            baseMessage += " with ID " + resultView.getExternalId();
        }
        baseMessage += " violates policy ";
        return baseMessage;
    }
}
