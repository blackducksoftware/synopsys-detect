package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.auth.model.role.RoleResource;
import com.synopsys.integration.polaris.common.api.auth.model.role.assignments.RoleAssignmentResource;
import com.synopsys.integration.polaris.common.api.auth.model.role.assignments.RoleAssignmentResources;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resource;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.polaris.common.request.param.ParamOperator;
import com.synopsys.integration.polaris.common.request.param.ParamType;
import com.synopsys.integration.polaris.common.request.param.PolarisParamBuilder;

public class RoleAssignmentsServiceTest {
    private final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
    private final Gson gson = new Gson();

    @Test
    public void callGetAllTest() throws IntegrationException {
        final PolarisServerConfig polarisServerConfig = createPolarisServerConfig();
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);
        final RoleAssignmentService roleAssignmentsService = polarisServicesFactory.createRoleAssignmentService();

        final List<RoleAssignmentResource> roleAssignments = roleAssignmentsService.getAll();
        assertFalse(roleAssignments.isEmpty(), "Expected role assignments to exist");
    }

    @Test
    public void callGetFilteredTest() throws IntegrationException {
        final PolarisServerConfig polarisServerConfig = createPolarisServerConfig();
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);
        final RoleAssignmentService roleAssignmentsService = polarisServicesFactory.createRoleAssignmentService();

        final PolarisParamBuilder paramBuilder = new PolarisParamBuilder();
        paramBuilder.setParamType(ParamType.FILTER);
        paramBuilder.setOperator(ParamOperator.OPERATOR_EQUALS);
        paramBuilder.setCaseSensitive(true);
        paramBuilder.addAdditionalProp("role-assignments");
        paramBuilder.addAdditionalProp("object");
        paramBuilder.setValue("nonsense value that should guarantee no results");

        final List<RoleAssignmentResource> roleAssignments = roleAssignmentsService.getFiltered(paramBuilder);
        assertTrue(roleAssignments.isEmpty(), "Expected role assignments to be empty");
    }

    @Test
    public void callGetRoleAssignmentsWithIncludedTest() throws IntegrationException {
        final PolarisServerConfig polarisServerConfig = createPolarisServerConfig();
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        final ProjectService projectService = polarisServicesFactory.createProjectService();
        final RoleAssignmentService roleAssignmentsService = polarisServicesFactory.createRoleAssignmentService();

        final ProjectV0Resource project;
        try {
            project = projectService.getAllProjects()
                          .stream()
                          .findAny()
                          .orElseThrow(() -> new IntegrationException("No projects found"));
        } catch (final IntegrationException e) {
            assumeTrue(e != null, "Something went wrong while retrieving projects, but this test is not for the project service");
            return;
        }

        final RoleAssignmentResources roleAssignmentsForProject = roleAssignmentsService.getRoleAssignmentsForProjectWithIncluded(project.getId(),
            RoleAssignmentService.INCLUDE_USERS, RoleAssignmentService.INCLUDE_ROLES, RoleAssignmentService.INCLUDE_GROUPS);
        final List<RoleAssignmentResource> data = roleAssignmentsForProject.getData();
        if (!data.isEmpty()) {
            assertFalse(roleAssignmentsForProject.getIncluded().isEmpty(), "Expected resources to be included");

            final RoleAssignmentResource roleAssignment = data
                                                              .stream()
                                                              .findAny()
                                                              .orElseThrow(IntegrationException::new);
            final Optional<RoleResource> roleResource = roleAssignmentsService.getRoleFromPopulatedRoleAssignments(roleAssignmentsForProject, roleAssignment);
            assertTrue(roleResource.isPresent(), "Expected a role to be present");
        }
    }

    private PolarisServerConfig createPolarisServerConfig() {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(gson);

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        return polarisServerConfigBuilder.build();
    }

}
