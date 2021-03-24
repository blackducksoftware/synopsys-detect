/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisResource;
import com.synopsys.integration.polaris.common.api.PolarisResourceSparse;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResource;
import com.synopsys.integration.polaris.common.api.auth.model.role.RoleResource;
import com.synopsys.integration.polaris.common.api.auth.model.role.assignments.RoleAssignmentRelationships;
import com.synopsys.integration.polaris.common.api.auth.model.role.assignments.RoleAssignmentResource;
import com.synopsys.integration.polaris.common.api.auth.model.role.assignments.RoleAssignmentResources;
import com.synopsys.integration.polaris.common.api.auth.model.user.UserResource;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestCreator;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.request.param.ParamOperator;
import com.synopsys.integration.polaris.common.request.param.ParamType;
import com.synopsys.integration.polaris.common.request.param.PolarisParamBuilder;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;

public class RoleAssignmentService {
    public static final String INCLUDE_GROUPS = "group";
    public static final String INCLUDE_ROLES = "role";
    public static final String INCLUDE_USERS = "user";

    final AccessTokenPolarisHttpClient polarisHttpClient;
    private final PolarisService polarisService;
    private final AuthService authService;

    public RoleAssignmentService(final AccessTokenPolarisHttpClient polarisHttpClient, final PolarisService polarisService, final AuthService authService) {
        this.polarisHttpClient = polarisHttpClient;
        this.polarisService = polarisService;
        this.authService = authService;
    }

    public List<RoleAssignmentResource> getAll() throws IntegrationException {
        return authService.getAll(AuthService.ROLE_ASSIGNMENTS_API_SPEC, RoleAssignmentResources.class);
    }

    public List<RoleAssignmentResource> getFiltered(final PolarisParamBuilder polarisParamBuilder) throws IntegrationException {
        return getFiltered(Collections.singletonList(polarisParamBuilder));
    }

    public List<RoleAssignmentResource> getFiltered(final Collection<PolarisParamBuilder> polarisParamBuilders) throws IntegrationException {
        return authService.getFiltered(AuthService.ROLE_ASSIGNMENTS_API_SPEC, polarisParamBuilders, RoleAssignmentResources.class);
    }

    public RoleAssignmentResources getRoleAssignmentsForProjectWithIncluded(final String projectId, final String... included) throws IntegrationException {
        final PolarisParamBuilder projectFilter = createProjectFilter(projectId);
        final PolarisPagedRequestCreator requestCreator = authService.generatePagedRequestCreatorWithInclude(AuthService.ROLE_ASSIGNMENTS_API_SPEC, projectFilter, included);
        final PolarisPagedRequestWrapper pagedRequestWrapper = new PolarisPagedRequestWrapper(requestCreator, RoleAssignmentResources.class);
        return polarisService.getPopulatedResponse(pagedRequestWrapper);
    }

    public Optional<GroupResource> getGroupFromPopulatedRoleAssignments(final RoleAssignmentResources populatedResources, final RoleAssignmentResource referencedResource) {
        return getResourceFromPopulatedRoleAssignments(populatedResources, referencedResource, RoleAssignmentRelationships::getGroup, GroupResource.class);
    }

    public Optional<UserResource> getUserFromPopulatedRoleAssignments(final RoleAssignmentResources populatedResources, final RoleAssignmentResource referencedResource) {
        return getResourceFromPopulatedRoleAssignments(populatedResources, referencedResource, RoleAssignmentRelationships::getUser, UserResource.class);
    }

    public Optional<RoleResource> getRoleFromPopulatedRoleAssignments(final RoleAssignmentResources populatedResources, final RoleAssignmentResource referencedResource) {
        return getResourceFromPopulatedRoleAssignments(populatedResources, referencedResource, RoleAssignmentRelationships::getRole, RoleResource.class);
    }

    public PolarisParamBuilder createProjectFilter(final String projectId) {
        return new PolarisParamBuilder()
                   .setValue("projects:" + projectId)
                   .setParamType(ParamType.FILTER)
                   .setOperator(ParamOperator.OPERATOR_SUBSTRING)
                   .addAdditionalProp("role-assignments")
                   .addAdditionalProp("object")
                   .setCaseSensitive(false);
    }

    private <R extends PolarisResource> Optional<R> getResourceFromPopulatedRoleAssignments(final RoleAssignmentResources populatedResources, final RoleAssignmentResource resourceReferenced,
        final Function<RoleAssignmentRelationships, PolarisRelationshipSingle> relationshipRetriever, final Class<R> resourceClass) {
        final Optional<PolarisResourceSparse> optionalResourceData = relationshipRetriever.apply(resourceReferenced.getRelationships()).getData();
        if (optionalResourceData.isPresent()) {
            return polarisService.getResourceFromPopulated(populatedResources, optionalResourceData.get(), resourceClass);
        }
        return Optional.empty();
    }

}
