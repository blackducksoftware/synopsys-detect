/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
