/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResource;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResources;
import com.synopsys.integration.polaris.common.request.param.ParamOperator;
import com.synopsys.integration.polaris.common.request.param.ParamType;
import com.synopsys.integration.polaris.common.request.param.PolarisParamBuilder;

public class GroupService {
    private final AuthService authService;

    public GroupService(final AuthService authService) {
        this.authService = authService;
    }

    public List<GroupResource> getAllGroups() throws IntegrationException {
        return authService.getAll(AuthService.GROUPS_API_SPEC, GroupResources.class);
    }

    public Optional<GroupResource> getGroupByName(final String groupName) throws IntegrationException {
        final PolarisParamBuilder groupNameFilter = createGroupNameFilter(groupName);
        final List<GroupResource> filteredGroups = authService.getFiltered(AuthService.GROUPS_API_SPEC, groupNameFilter, GroupResources.class);
        return filteredGroups
                   .stream()
                   .findFirst();
    }

    private PolarisParamBuilder createGroupNameFilter(final String groupName) {
        return new PolarisParamBuilder()
                   .setValue(groupName)
                   .setParamType(ParamType.FILTER)
                   .setOperator(ParamOperator.OPERATOR_EQUALS)
                   .addAdditionalProp("groups")
                   .addAdditionalProp("groupname")
                   .setCaseSensitive(true);
    }

}
