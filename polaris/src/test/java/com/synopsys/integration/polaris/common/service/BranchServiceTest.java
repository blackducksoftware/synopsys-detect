package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resource;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class BranchServiceTest {
    @Test
    public void callGetBranchesForProjectTest() throws IntegrationException {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(new Gson());

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        final ProjectService projectService = polarisServicesFactory.createProjectService();

        final ProjectV0Resource project;
        try {
            project = projectService.getAllProjects()
                          .stream()
                          .findAny()
                          .orElseThrow(IntegrationException::new);
        } catch (final IntegrationException e) {
            assumeTrue(null != e, "Something went wrong while retrieving projects, but this test is not for the project service");
            return;
        }

        final BranchService branchService = polarisServicesFactory.createBranchService();
        branchService.getBranchesForProject(project.getId());
    }

}
