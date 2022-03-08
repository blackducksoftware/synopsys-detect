package com.synopsys.integration.detect.battery.docker.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckAssertions {
    private final ProjectService projectService;
    private final BlackDuckApiClient blackDuckApiClient;
    private final ProjectBomService projectBomService;
    private final NameVersion projectNameVersion;

    public BlackDuckAssertions(BlackDuckServicesFactory blackDuckServicesFactory, NameVersion projectNameVersion) {
        projectService = blackDuckServicesFactory.createProjectService();
        blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
        projectBomService = blackDuckServicesFactory.createProjectBomService();
        this.projectNameVersion = projectNameVersion;
    }

    public ProjectVersionWrapper emptyOnBlackDuck() throws IntegrationException {
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectNameVersion);
        if (optionalProjectVersionWrapper.isPresent()) {
            blackDuckApiClient.delete(optionalProjectVersionWrapper.get().getProjectView());
        }

        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectNameVersion);
        projectService.syncProjectAndVersion(projectSyncModel);

        optionalProjectVersionWrapper = projectService.getProjectVersion(projectNameVersion);
        assertTrue(optionalProjectVersionWrapper.isPresent());

        List<CodeLocationView> codeLocations = blackDuckApiClient.getAllResponses(optionalProjectVersionWrapper.get().getProjectVersionView().metaCodelocationsLink());
        assertEquals(0, codeLocations.size());

        List<ProjectVersionComponentVersionView> bomComponents = projectBomService.getComponentsForProjectVersion(optionalProjectVersionWrapper.get().getProjectVersionView());
        assertEquals(0, bomComponents.size());

        return optionalProjectVersionWrapper.get();
    }

    public void deleteProjectAndCodeLocations() throws IntegrationException {
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectNameVersion);
        if (!optionalProjectVersionWrapper.isPresent()) {
            return;
        }
        ProjectVersionWrapper projectVersionWrapper = optionalProjectVersionWrapper.get();
        List<CodeLocationView> codeLocationsToDelete = blackDuckApiClient.getAllResponses(projectVersionWrapper.getProjectVersionView().metaCodelocationsLink());
        for (CodeLocationView toDelete : codeLocationsToDelete) {
            blackDuckApiClient.delete(toDelete);
        }

        blackDuckApiClient.delete(projectVersionWrapper.getProjectView());
    }

    public ProjectVersionWrapper retrieveProjectVersionWrapper() throws IntegrationException {
        return projectService.getProjectVersion(projectNameVersion).orElseThrow(() -> new RuntimeException("Project Version expected but could not be found on Black Duck."));
    }

    public void assertExactCodeLocations(Set<String> codeLocationNames) throws IntegrationException {
        List<CodeLocationView> codeLocationsToDelete = blackDuckApiClient.getAllResponses(retrieveProjectVersionWrapper().getProjectVersionView().metaCodelocationsLink());
        Set<String> createdCodeLocationNames = codeLocationsToDelete.stream().map(CodeLocationView::getName).collect(Collectors.toSet());

        SetAssertionUtil.assertSetDifferences(createdCodeLocationNames, codeLocationNames,
            expectedMissing -> Assertions.fail(String.format("Expected code location %s but could not find it!", expectedMissing)),
            extraActual -> Assertions.fail(String.format("An additional code location %s was found but was not expected!", extraActual))
        );
    }

    public void hasCodeLocations(String... codeLocationNames) throws IntegrationException {
        hasCodeLocations(Bds.of(codeLocationNames).toSet());
    }

    public void hasCodeLocations(Set<String> codeLocationNames) throws IntegrationException {
        List<CodeLocationView> codeLocationsToDelete = blackDuckApiClient.getAllResponses(retrieveProjectVersionWrapper().getProjectVersionView().metaCodelocationsLink());
        Set<String> createdCodeLocationNames = codeLocationsToDelete.stream().map(CodeLocationView::getName).collect(Collectors.toSet());

        SetAssertionUtil.assertSetDifferences(createdCodeLocationNames, codeLocationNames,
            expectedMissing -> Assertions.fail(String.format("Expected code location %s but could not find it!", expectedMissing)),
            extraActual -> {/* no-op, extra code locations fine, only checking it HAS the given. */}
        );
    }

    public void hasComponents(String... componentNames) throws IntegrationException {
        hasComponents(Bds.of(componentNames).toSet());
    }

    public void hasComponents(Set<String> componentNames) throws IntegrationException {
        List<ProjectVersionComponentVersionView> bomComponents = projectBomService.getComponentsForProjectVersion(retrieveProjectVersionWrapper().getProjectVersionView());
        componentNames.forEach(componentName -> {
            Optional<ProjectVersionComponentVersionView> blackDuckCommonComponent = bomComponents.stream()
                .filter(ProjectVersionComponentView -> componentName.equals(ProjectVersionComponentView.getComponentName()))
                .findFirst();
            assertTrue(blackDuckCommonComponent.isPresent());
        });
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public ProjectView retrieveProjectView() throws IntegrationException {
        return retrieveProjectVersionWrapper().getProjectView();
    }

    public void codeLocationCount(int codeLocationCount) throws IntegrationException {
        Assertions.assertEquals(codeLocationCount, blackDuckApiClient.getAllResponses(retrieveProjectVersionWrapper().getProjectVersionView().metaCodelocationsLink()).size());
    }
}
