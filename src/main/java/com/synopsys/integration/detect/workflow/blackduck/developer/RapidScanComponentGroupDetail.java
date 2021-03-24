/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.LinkedHashSet;
import java.util.Set;

class RapidScanComponentGroupDetail {
    private RapidScanDetailGroup group;
    private Set<String> errorMessages = new LinkedHashSet<>();
    private Set<String> warningMessages = new LinkedHashSet<>();
    private Set<String> policyNames = new LinkedHashSet<>();

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

}
