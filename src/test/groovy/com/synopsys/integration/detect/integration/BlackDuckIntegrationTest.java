package com.synopsys.integration.detect.integration;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.VersionBomComponentView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.*;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlackDuckIntegrationTest {
    public static final String TEST_BLACKDUCK_URL_KEY = "TEST_BLACKDUCK_URL";
    public static final String TEST_BLACKDUCK_USERNAME_KEY = "TEST_BLACKDUCK_USERNAME";
    public static final String TEST_BLACKDUCK_PASSWORD_KEY = "TEST_BLACKDUCK_PASSWORD";

    protected static IntLogger logger;
    protected static BlackDuckServicesFactory blackDuckServicesFactory;
    protected static BlackDuckService blackDuckService;
    protected static ProjectService projectService;
    protected static ProjectBomService projectBomService;
    protected static CodeLocationService codeLocationService;
    protected static ReportService reportService;
    protected static boolean previousShouldExit;

    @BeforeAll
    public static void setup() throws Exception {
        logger = new BufferedIntLogger();

        Assumptions.assumeTrue(StringUtils.isNotBlank(System.getenv().get(TEST_BLACKDUCK_URL_KEY)));
        Assumptions.assumeTrue(StringUtils.isNotBlank(System.getenv().get(TEST_BLACKDUCK_USERNAME_KEY)));
        Assumptions.assumeTrue(StringUtils.isNotBlank(System.getenv().get(TEST_BLACKDUCK_PASSWORD_KEY)));

        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());
        blackDuckServerConfigBuilder.setUrl(System.getenv().get(TEST_BLACKDUCK_URL_KEY));
        blackDuckServerConfigBuilder.setUsername(System.getenv().get(TEST_BLACKDUCK_USERNAME_KEY));
        blackDuckServerConfigBuilder.setPassword(System.getenv().get(TEST_BLACKDUCK_PASSWORD_KEY));

        blackDuckServicesFactory = blackDuckServerConfigBuilder.build().createBlackDuckServicesFactory(logger);
        blackDuckService = blackDuckServicesFactory.createBlackDuckService();
        projectService = blackDuckServicesFactory.createProjectService();
        projectBomService = blackDuckServicesFactory.createProjectBomService();
        codeLocationService = blackDuckServicesFactory.createCodeLocationService();
        reportService = blackDuckServicesFactory.createReportService(120 * 1000);

        previousShouldExit = Application.SHOULD_EXIT;
        Application.SHOULD_EXIT = false;
    }

    @AfterAll
    public static void cleanup() {
        Application.SHOULD_EXIT = previousShouldExit;
    }

    public ProjectVersionWrapper assertProjectVersionReady(String projectName, String projectVersionName) throws IntegrationException {
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        if (optionalProjectVersionWrapper.isPresent()) {
            blackDuckService.delete(optionalProjectVersionWrapper.get().getProjectView());
        }

        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectName, projectVersionName);
        projectService.syncProjectAndVersion(projectSyncModel);
        optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        assertTrue(optionalProjectVersionWrapper.isPresent());

        List<CodeLocationView> codeLocations = blackDuckService.getAllResponses(optionalProjectVersionWrapper.get().getProjectVersionView(), ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);
        assertEquals(0, codeLocations.size());

        List<VersionBomComponentView> bomComponents = projectBomService.getComponentsForProjectVersion(optionalProjectVersionWrapper.get().getProjectVersionView());
        assertEquals(0, bomComponents.size());

        return optionalProjectVersionWrapper.get();
    }

    public static List<String> getInitialArgs(String projectName, String projectVersionName) {
        List<String> initialArgs = new ArrayList<>();
        initialArgs.add("--detect.tools.excluded=POLARIS");
        initialArgs.add("--detect.project.name=" + projectName);
        initialArgs.add("--detect.project.version.name=" + projectVersionName);
        initialArgs.add("--blackduck.url=" + System.getenv().get(TEST_BLACKDUCK_URL_KEY));
        initialArgs.add("--blackduck.username=" + System.getenv().get(TEST_BLACKDUCK_USERNAME_KEY));
        initialArgs.add("--blackduck.password=" + System.getenv().get(TEST_BLACKDUCK_PASSWORD_KEY));

        return initialArgs;
    }

}
