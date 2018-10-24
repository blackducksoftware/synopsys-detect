package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(RunManager.class);

    private final PhoneHomeManager phoneHomeManager;
    private final DetectConfiguration detectConfiguration;
    private final BomToolManager bomToolManager;
    private ProjectNameVersionManager projectNameVersionManager;
    private DetectProjectService detectProjectService;
    private DetectCodeLocationUnmapService detectCodeLocationUnmapService;
    private BdioManager bdioManager;
    private DetectBdioUploadService detectBdioUploadService;
    private HubManager hubManager;
    private HubService hubService;

    public RunManager(final PhoneHomeManager phoneHomeManager, final DetectConfiguration detectConfiguration, final BomToolManager bomToolManager,
        final ProjectNameVersionManager projectNameVersionManager, final DetectProjectService detectProjectService,
        final DetectCodeLocationUnmapService detectCodeLocationUnmapService, final BdioManager bdioManager, final DetectBdioUploadService detectBdioUploadService,
        final HubManager hubManager, final HubService hubService) {

        this.phoneHomeManager = phoneHomeManager;
        this.detectConfiguration = detectConfiguration;

        this.bomToolManager = bomToolManager;

        this.projectNameVersionManager = projectNameVersionManager;
        this.detectProjectService = detectProjectService;
        this.detectCodeLocationUnmapService = detectCodeLocationUnmapService;
        this.bdioManager = bdioManager;
        this.detectBdioUploadService = detectBdioUploadService;
        this.hubManager = hubManager;
        this.hubService = hubService;
    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        phoneHomeManager.startPhoneHome();

        boolean bomToolsEnabled = !this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED, PropertyAuthority.None);
        boolean sigScanEnabled = !this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None);
        boolean isOnline = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);

        Optional<NameVersion> projectNameVersion = Optional.empty();
        Optional<ProjectVersionView> projectView = Optional.empty();

        if (bomToolsEnabled) {
            BomToolsResult bomToolsResult = bomToolManager.runBomTools();
            projectNameVersion = Optional.of(projectNameVersionManager.evaluateProjectNameVersion(bomToolsResult.evaluatedBomTools));

            if (isOnline) {
                projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
                if (projectView.isPresent() && unmapCodeLocations) {
                    detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
                }
            }

            BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, projectNameVersion.get(), bomToolsResult.bomToolCodeLocations);
            if (bdioResult.getBdioFiles().size() > 0) {
                if (isOnline) {
                    detectBdioUploadService.uploadBdioFiles(bdioResult.getBdioFiles());
                } else {
                    //TODO: Let diagnostics know about bdio.
                }
            } else {
                logger.debug("Did not create any bdio files.");
            }
        }

        if (!projectNameVersion.isPresent()) {
            projectNameVersion = Optional.of(projectNameVersionManager.calculateDefaultProjectNameVersion());
            projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
        }

        if (isOnline) {
            hubManager.performScanActions(projectNameVersion.get());
            hubManager.performBinaryScanActions(projectNameVersion.get());
            if (projectView.isPresent()) {
                hubManager.performPostHubActions(projectNameVersion.get(), projectView.get());
            }

            //TODO: replicate bdio file list check (if bdioFiles.exist && scanWasRan)
            final String componentsLink = hubService.getFirstLinkSafely(projectView.get(), ProjectVersionView.COMPONENTS_LINK);
            logger.info(String.format("To see your results, follow the URL: %s", componentsLink));

        } else {
            hubManager.performScanActions(projectNameVersion.get());
        }
    }
}
