package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsComponentViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesView;
import com.synopsys.integration.blackduck.api.generated.component.ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView;
import com.synopsys.integration.blackduck.api.generated.view.ScanFullResultView;

public class RapidScanComponentGroupDetail {
    
    private static final String POLICY_SEPARATOR = "/";
    private static final String POLICY_SEVERITY_BLOCKER = "BLOCKER";
    private static final String POLICY_SEVERITY_CRITICAL = "CRITICAL";
    
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

    // TODO rapid scans are now using the v5 developer-scans endpoint which no longer constructs 
    // warning and error messages. Until BlackDuck adds that back we have to construct our own messages.
    // While it may be possible to reduce the overall message generation code in this class by pushing 
    // some common pieces into a parent class or interface, it is likely not worth altering the libraries 
    // as this may be temporary code.
    public void addComponentMessages(ScanFullResultView resultView, ScanFullResultItemsComponentViolatingPoliciesView componentPolicyViolation) {
        String baseMessage = getBaseMessage(resultView);

        String errorMessage = "", warningMessage = "";

            if (componentPolicyViolation.getPolicySeverity().equals(POLICY_SEVERITY_CRITICAL) || componentPolicyViolation.getPolicySeverity().equals(POLICY_SEVERITY_BLOCKER)) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += ", ";
                }
                
                errorMessage += componentPolicyViolation.getPolicyName();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += ", ";
                }
                
                warningMessage += componentPolicyViolation.getPolicyName();
            }
            
        addMessages(errorMessage, warningMessage);
    }

    // TODO rapid scans are now using the v5 developer-scans endpoint which no longer constructs 
    // warning and error messages. Until BlackDuck adds that back we have to construct our own messages.
    // While it may be possible to reduce the overall message generation code in this class by pushing 
    // some common pieces into a parent class or interface, it is likely not worth altering the libraries 
    // as this may be temporary code.
    public void addLicenseMessages(ScanFullResultView resultView, ScanFullResultItemsPolicyViolationLicensesView licensePolicyViolation) {
        String baseMessage = getBaseMessage(resultView);
        
        List<ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView> violatingPolicies = licensePolicyViolation.getViolatingPolicies();
        
        String errorMessage = "", warningMessage = "";
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            ScanFullResultItemsPolicyViolationLicensesViolatingPoliciesView violation = violatingPolicies.get(i);
                    
            if (violation.getPolicySeverity().equals(POLICY_SEVERITY_CRITICAL) || violation.getPolicySeverity().equals(POLICY_SEVERITY_BLOCKER)) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += POLICY_SEPARATOR;
                }
                
                errorMessage += violation.getPolicyName();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += POLICY_SEPARATOR;
                }
                
                warningMessage += violation.getPolicyName();
            }
        }
        
        String summaryMessage = ": license " + licensePolicyViolation.getName();
        
        if (StringUtils.isNotBlank(errorMessage)) {
            errorMessage += summaryMessage;
        }
        if (StringUtils.isNotBlank(warningMessage)) {
            warningMessage += summaryMessage;
        }
        
        addMessages(errorMessage, warningMessage);
    }
    
    // TODO rapid scans are now using the v5 developer-scans endpoint which no longer constructs 
    // warning and error messages. Until BlackDuck adds that back we have to construct our own messages.
    // While it may be possible to reduce the overall message generation code in this class by pushing 
    // some common pieces into a parent class or interface, it is likely not worth altering the libraries 
    // as this may be temporary code.
    public void addVulnerabilityMessages(ScanFullResultView resultView,
            ScanFullResultItemsPolicyViolationVulnerabilitiesView vulnerability) {
        String baseMessage = getBaseMessage(resultView);
        
        List<ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView> violatingPolicies = vulnerability.getViolatingPolicies();
        
        String errorMessage = "", warningMessage = "";
        
        for (int i = 0; i < violatingPolicies.size(); i++) {
            ScanFullResultItemsPolicyViolationVulnerabilitiesViolatingPoliciesView violation = violatingPolicies.get(i);
            
            if (violation.getPolicySeverity().equals(POLICY_SEVERITY_CRITICAL) || violation.getPolicySeverity().equals(POLICY_SEVERITY_BLOCKER)) {
                if (errorMessage.equals("")) {
                    errorMessage = baseMessage;
                } else {
                    errorMessage += POLICY_SEPARATOR;
                }
                
                errorMessage += violation.getPolicyName();
            } else {
                if (warningMessage.equals("")) {
                    warningMessage = baseMessage;
                } else {
                    warningMessage += POLICY_SEPARATOR;
                }
                
                warningMessage += violation.getPolicyName();
            }
        }
        
        String summaryMessage = ": found vulnerability " + vulnerability.getName() + " with severity "
                + vulnerability.getVulnSeverity() + " and CVSS score " + vulnerability.getOverallScore() + ".";
        
        if (StringUtils.isNotBlank(errorMessage)) {
            errorMessage += summaryMessage;
        }
        if (StringUtils.isNotBlank(warningMessage)) {
            warningMessage += summaryMessage;
        }
        
        if (resultView.getLongTermUpgradeGuidance() != null && resultView.getShortTermUpgradeGuidance() != null) {
            String upgradeGuidance = " Long term upgrade guidance: "
                    + resultView.getLongTermUpgradeGuidance().getVersionName() + ", short term upgrade guidance "
                    + resultView.getShortTermUpgradeGuidance().getVersionName();

            if (StringUtils.isNotBlank(errorMessage)) {
                errorMessage += upgradeGuidance;
            }
            if (StringUtils.isNotBlank(warningMessage)) {
                warningMessage += upgradeGuidance;
            }
        }
        
        addMessages(errorMessage, warningMessage);
    }
    
    private String getBaseMessage(ScanFullResultView resultView) {
        String baseMessage = "Component " + resultView.getComponentName() + " version " + resultView.getVersionName();
        if (StringUtils.isNotBlank(resultView.getExternalId())) {
            baseMessage += " with ID " + resultView.getExternalId();
        }
        baseMessage += " violates policy ";
        return baseMessage;
    }
}
