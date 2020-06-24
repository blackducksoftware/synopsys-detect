/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRuleChooser {

    @NotNull
    public Set<WorkspaceRule> choose(Set<WorkspaceRule> rulesFromWorkspaceFile, List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) throws IntegrationException {
        Set<WorkspaceRule> cleanedUserProvidedRules = clean(userProvidedRules);
        if (!cleanedUserProvidedRules.isEmpty()) {
            return cleanedUserProvidedRules;
        } else if (!rulesFromWorkspaceFile.isEmpty()) {
            return rulesFromWorkspaceFile;
        } else {
            throw new IntegrationException("Unable to determine BazelWorkspace dependency rule type; try setting it via the property");
        }
    }

    // We continue to support UNSPECIFIED to avoid making a breaking change
    private Set<WorkspaceRule> clean(List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) {
        Set<WorkspaceRule> cleanedRulesList = new HashSet<>();
        if (noneSpecified(userProvidedRules)) {
            // Leave cleanedRulesList empty
        } else if (allSpecified(userProvidedRules)) {
            addAllRuleTypes(cleanedRulesList);
        } else {
            addUserProvidedOmittingUnspecified(cleanedRulesList, userProvidedRules);
        }
        return cleanedRulesList;
    }

    private void addUserProvidedOmittingUnspecified(Set<WorkspaceRule> cleanedRulesList, List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) {
        for (FilterableEnumValue<WorkspaceRule> givenRule : userProvidedRules) {
            if (givenRule.getValue().isPresent() && givenRule.getValue().get() != WorkspaceRule.UNSPECIFIED) {
                cleanedRulesList.add(givenRule.getValue().get());
            }
        }
    }

    private void addAllRuleTypes(Set<WorkspaceRule> cleanedRulesList) {
        for (WorkspaceRule rule : WorkspaceRule.values()) {
            if (rule != rule.UNSPECIFIED) {
                cleanedRulesList.add(rule);
            }
        }
    }

    private boolean noneSpecified(List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) {
        if (userProvidedRules == null ||
                userProvidedRules.isEmpty() ||
                FilterableEnumUtils.containsNone(userProvidedRules) ||
                (userProvidedRules.size() == 1 && ((!FilterableEnumUtils.containsAll(userProvidedRules)) && !userProvidedRules.get(0).getValue().isPresent())) ||
                (userProvidedRules.size() == 1 && userProvidedRules.get(0).getValue().isPresent() &&
                     userProvidedRules.get(0).getValue().get() == WorkspaceRule.UNSPECIFIED)) {
            return true;
        }
        return false;
    }

    private boolean allSpecified(List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) {
        if (userProvidedRules != null && FilterableEnumUtils.containsAll(userProvidedRules)) {
            return true;
        }
        return false;
    }

    // TODO take advantage of FilterableEnumUtils.toPresentValues() and populatedValues() (and others?)
}
