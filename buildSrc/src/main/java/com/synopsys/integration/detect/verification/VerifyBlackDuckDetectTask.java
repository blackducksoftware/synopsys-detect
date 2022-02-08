package com.synopsys.integration.detect.verification;

import static com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView.COMPONENTS_LINK;

import java.util.List;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import com.synopsys.integration.blackduck.api.core.response.LinkMultipleResponses;
import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentVersionView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.PrintStreamIntLogger;

public class VerifyBlackDuckDetectTask extends DefaultTask {
    @TaskAction
    public void verifyBlackDuckAction() throws IntegrationException {
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newApiTokenBuilder();
        Set<String> environmentKeys = blackDuckServerConfigBuilder.getEnvironmentVariableKeys();
        environmentKeys.forEach(it -> {
            String value = System.getenv().get(it);
            if (value != null) {
                blackDuckServerConfigBuilder.setProperty(it, value);
            }
        });

        BlackDuckServerConfig blackDuckServerConfig = blackDuckServerConfigBuilder.build();
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckServerConfig.createBlackDuckServicesFactory(new PrintStreamIntLogger(System.out, com.synopsys.integration.log.LogLevel.INFO));
        BlackDuckApiClient blackDuckService = blackDuckServicesFactory.getBlackDuckApiClient();
        ProjectService projectService = blackDuckServicesFactory.createProjectService();

        ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(getProject().getName(), getProject().getVersion().toString()).get();
        // TODO: ProjectVersionView::metaComponentsLink appears to return the incorrect type. I had to manually construct the correct COMPONENTS_LINK. -JM 07/2021
        LinkMultipleResponses<ProjectVersionComponentVersionView> linkMultipleResponses = new LinkMultipleResponses<>(COMPONENTS_LINK, ProjectVersionComponentVersionView.class);
        UrlMultipleResponses<ProjectVersionComponentVersionView> urlMultipleResponses = projectVersionWrapper.getProjectVersionView().metaMultipleResponses(linkMultipleResponses);
        List<ProjectVersionComponentVersionView> bomComponents = blackDuckService.getAllResponses(urlMultipleResponses);
        if (bomComponents.isEmpty()) {
            throw new GradleException("No bom components were found for ${project.name} - ${version}");
        }
        bomComponents.forEach(it -> System.out.println(it.getComponentName() + " - " + it.getComponentVersionName()));
    }

}
