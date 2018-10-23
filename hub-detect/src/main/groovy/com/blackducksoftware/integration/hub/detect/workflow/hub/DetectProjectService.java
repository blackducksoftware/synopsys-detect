package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.synopsys.integration.blackduck.api.generated.component.ProjectRequest;
import com.synopsys.integration.blackduck.api.generated.component.ProjectVersionRequest;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.exception.DoesNotExistException;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectRequestBuilder;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class DetectProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HubServiceManager hubServiceManager;
    private final DetectProjectServiceOptions detectProjectServiceOptions;

    public DetectProjectService(final HubServiceManager hubServiceManager, final DetectProjectServiceOptions detectProjectServiceOptions) {
        this.hubServiceManager = hubServiceManager;
        this.detectProjectServiceOptions = detectProjectServiceOptions;
    }

    public Optional<ProjectVersionView> createOrUpdateHubProject(NameVersion projectNameVersion) throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        final ProjectService projectService = hubServiceManager.createProjectService();
        final HubService hubService = hubServiceManager.createHubService();
        final ProjectVersionView projectVersionView = ensureProjectVersionExists(projectNameVersion, projectService, hubService);
        return Optional.ofNullable(projectVersionView);
    }

    private ProjectVersionView getProjectVersionAndUpdateOrCreateIfNeeded(final ProjectRequest projectRequest, final ProjectService projectService, final HubService hubService, final boolean forceUpdate) throws IntegrationException {
        ProjectView project = null;
        ProjectVersionView projectVersion = null;
        boolean shouldUpdateProject = true;
        try {
            logger.debug("Checking for project.");
            project = projectService.getProjectByName(projectRequest.name);
        } catch (final DoesNotExistException e) {
            logger.debug("Creating project.");
            final String projectURL = projectService.createHubProject(projectRequest);
            project = hubService.getResponse(projectURL, ProjectView.class);
            shouldUpdateProject = false;
        }

        if (forceUpdate && shouldUpdateProject) {
            logger.debug("Updating project.");
            final ProjectVersionRequest cachedRequest = projectRequest.versionRequest;
            projectRequest.versionRequest = null;
            projectService.updateProjectAndVersion(project, projectRequest);
            projectRequest.versionRequest = cachedRequest;
        }

        boolean shouldUpdateVersion = true;
        try {
            logger.debug("Checking for version.");
            projectVersion = projectService.getProjectVersion(project, projectRequest.versionRequest.versionName);
        } catch (final DoesNotExistException e) {
            logger.debug("Creating version.");
            final String versionURL = projectService.createHubVersion(project, projectRequest.versionRequest);
            projectVersion = hubService.getResponse(versionURL, ProjectVersionView.class);
            shouldUpdateVersion = false;
        }

        if (forceUpdate && shouldUpdateVersion) {
            logger.debug("Updating version.");
            projectService.updateProjectAndVersion(project, projectRequest);
        }

        return projectVersion;
    }

    public ProjectRequest createProjectRequest(final NameVersion projectNameVersion, final ProjectService projectService, final HubService hubService) throws DetectUserFriendlyException {
        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();

        projectRequestBuilder.setProjectName(projectNameVersion.getName());
        projectRequestBuilder.setVersionName(projectNameVersion.getVersion());

        projectRequestBuilder.setProjectLevelAdjustments(detectProjectServiceOptions.isProjectLevelAdjustments());
        projectRequestBuilder.setPhase(detectProjectServiceOptions.getProjectVersionPhase());
        projectRequestBuilder.setDistribution(detectProjectServiceOptions.getProjectVersionDistribution());
        projectRequestBuilder.setProjectTier(detectProjectServiceOptions.getProjectTier());
        projectRequestBuilder.setDescription(detectProjectServiceOptions.getProjectDescription());
        projectRequestBuilder.setReleaseComments(detectProjectServiceOptions.getProjectVersionNotes());
        projectRequestBuilder.setCloneCategories(convertClonePropertyToEnum(detectProjectServiceOptions.getCloneCategories()));

        final Optional<String> cloneUrl = findCloneUrl(projectNameVersion, projectService, hubService);
        if (cloneUrl.isPresent()) {
            logger.info("Cloning project version from release url: " + cloneUrl.get());
            projectRequestBuilder.setCloneFromReleaseUrl(cloneUrl.get());
        }

        return projectRequestBuilder.build();
    }

    public ProjectVersionView ensureProjectVersionExists(NameVersion projectNameVersion, final ProjectService projectService, final HubService hubService) throws IntegrationException, DetectUserFriendlyException {
        final ProjectRequest projectRequest = createProjectRequest(projectNameVersion, projectService, hubService);

        final boolean forceUpdate = detectProjectServiceOptions.isForceProjectVersionUpdate();

        return getProjectVersionAndUpdateOrCreateIfNeeded(projectRequest, projectService, hubService, forceUpdate);
    }

    private List<ProjectCloneCategoriesType> convertClonePropertyToEnum(final String[] cloneCategories) {
        final List<ProjectCloneCategoriesType> categories = new ArrayList<>();
        for (final String category : cloneCategories) {
            categories.add(ProjectCloneCategoriesType.valueOf(category));
        }
        logger.debug("Found clone categories:" + categories.stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        return categories;
    }

    public Optional<String> findCloneUrl(NameVersion projectNameVersion, final ProjectService projectService, final HubService hubService) throws DetectUserFriendlyException {
        final String cloneProjectName = projectNameVersion.getName();
        final String cloneProjectVersionName = detectProjectServiceOptions.getCloneVersionName();
        if (StringUtils.isBlank(cloneProjectName) || StringUtils.isBlank(cloneProjectVersionName)) {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return Optional.empty();
        }
        try {
            final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(cloneProjectName, cloneProjectVersionName);
            final String url = hubService.getHref(projectVersionWrapper.getProjectVersionView());
            return Optional.of(url);
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException("Unable to find clone release url for supplied clone version name.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

    }
}
