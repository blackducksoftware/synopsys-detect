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

import java.util.Arrays;
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
    public Set<WorkspaceRule> choose(Set<WorkspaceRule> rulesFromWorkspaceFile, List<FilterableEnumValue<WorkspaceRule>> rulesPropertyValues) throws IntegrationException {
        Set<WorkspaceRule> cleanedUserProvidedRules = translateToRules(rulesPropertyValues);
        if (!cleanedUserProvidedRules.isEmpty()) {
            return cleanedUserProvidedRules;
        } else if (!rulesFromWorkspaceFile.isEmpty()) {
            return rulesFromWorkspaceFile;
        } else {
            throw new IntegrationException("Unable to determine BazelWorkspace dependency rule type; try setting it via the property");
        }
    }

    private Set<WorkspaceRule> translateToRules(List<FilterableEnumValue<WorkspaceRule>> rulesPropertyValues) {
        Set<WorkspaceRule> cleanedRulesList = new HashSet<>();
        if (noneSpecified(rulesPropertyValues)) {
            // Leave cleanedRulesList empty
        } else if (allSpecified(rulesPropertyValues)) {
            cleanedRulesList.addAll(Arrays.asList(WorkspaceRule.values()));
        } else {
            cleanedRulesList.addAll(FilterableEnumUtils.toPresentValues(rulesPropertyValues));
        }
        return cleanedRulesList;
    }

    private boolean noneSpecified(List<FilterableEnumValue<WorkspaceRule>> rulesPropertyValues) {
        if (rulesPropertyValues == null ||
                FilterableEnumUtils.containsNone(rulesPropertyValues) ||
                (FilterableEnumUtils.toPresentValues(rulesPropertyValues).isEmpty() && !FilterableEnumUtils.containsAll(rulesPropertyValues))) {
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
}
