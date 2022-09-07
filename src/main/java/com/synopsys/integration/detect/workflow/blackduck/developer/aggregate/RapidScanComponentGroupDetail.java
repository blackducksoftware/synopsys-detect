package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

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
}
