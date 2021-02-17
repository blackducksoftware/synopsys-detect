/*
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipMultiple;
import com.synopsys.integration.polaris.common.api.PolarisResource;
import com.synopsys.integration.polaris.common.api.PolarisResourceSparse;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResource;
import com.synopsys.integration.polaris.common.api.auth.model.user.EmailDetailsResource;
import com.synopsys.integration.polaris.common.api.auth.model.user.EmailDetailsResources;
import com.synopsys.integration.polaris.common.api.auth.model.user.UserResource;
import com.synopsys.integration.polaris.common.api.auth.model.user.UserResources;

public class UserService {
    private final AuthService authService;

    public UserService(final AuthService authService) {
        this.authService = authService;
    }

    public List<UserResource> getAllUsers() throws IntegrationException {
        return authService.getAll(AuthService.USERS_API_SPEC, UserResources.class);
    }

    public Set<UserResource> getUsersForGroup(final GroupResource group) throws IntegrationException {
        return getUsersForGroups(Collections.singletonList(group));
    }

    public Set<UserResource> getUsersForGroups(final List<GroupResource> groups) throws IntegrationException {
        final List<UserResource> users = getAllUsers();
        return getUsersForGroups(users, groups);
    }

    public Set<UserResource> getUsersForGroup(final List<UserResource> users, final GroupResource group) {
        return getUsersForGroups(users, Collections.singletonList(group));
    }

    public Set<UserResource> getUsersForGroups(final List<UserResource> users, final List<GroupResource> groups) {
        final Set<UserResource> usersForGroups = new HashSet<>();
        final Set<String> groupIds = groups
                                         .stream()
                                         .map(PolarisResource::getId)
                                         .collect(Collectors.toSet());
        for (final UserResource user : users) {
            final PolarisRelationshipMultiple userGroupRelationship = user.getRelationships().getGroups();
            for (final PolarisResourceSparse sparseGroup : userGroupRelationship.getData()) {
                if (groupIds.contains(sparseGroup.getId())) {
                    usersForGroups.add(user);
                    break;
                }
            }
        }
        return usersForGroups;
    }

    public Optional<String> getEmailForUser(final UserResource user) throws IntegrationException {
        final String email = user.getAttributes().getEmail();
        if (StringUtils.isNotBlank(email)) {
            return Optional.of(email);
        } else {
            final PolarisRelationship emailDetails = user.getRelationships().getEmailDetails();
            return authService.getAttributeFromRelationship(emailDetails.getLinks(), (EmailDetailsResource resource) -> resource.getAttributes().getEmail(), EmailDetailsResources.class);
        }
    }

}
