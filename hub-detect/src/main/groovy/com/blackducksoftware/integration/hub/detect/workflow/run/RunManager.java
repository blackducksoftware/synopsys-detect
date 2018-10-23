package com.blackducksoftware.integration.hub.detect.workflow.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.exit.ExitCodeManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(RunManager.class);

    private final PhoneHomeManager phoneHomeManager;
    private final DetectConfiguration detectConfiguration;
    private final BomToolManager bomToolManager;
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final EventSystem eventSystem;
    private ProjectNameVersionManager projectNameVersionManager;
    private final DetectProjectService detectProjectService;
    private final DetectBdioUploadService detectBdioService;
    private final DetectCodeLocationUnmapService detectCodeLocationUnmapService;
    private final BdioManager bdioManager;
    private final DetectBdioUploadService detectBdioUploadService;
    private final DetectCodeLocationManager detectCodeLocationManager;

    public RunManager(final PhoneHomeManager phoneHomeManager, final DetectConfiguration detectConfiguration, final BomToolManager bomToolManager,
        final DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager, EventSystem eventSystem) {

        this.phoneHomeManager = phoneHomeManager;
        this.detectConfiguration = detectConfiguration;

        this.bomToolManager = bomToolManager;
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.eventSystem = eventSystem;

    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        phoneHomeManager.startPhoneHome();

        boolean bomToolsEnabled = !this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED, PropertyAuthority.None);
        boolean sigScanEnabled = !this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None);
        boolean isOnline = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        boolean checkHubConnectionOnly = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION, PropertyAuthority.None);
        boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);

        if (checkHubConnectionOnly) {

        }

        if (bomToolsEnabled) {
            BomToolResult bomToolResult = bomToolManager.runBomTools();
            final NameVersion bomToolProjectInfo = projectNameVersionManager.evaluateProjectNameVersion(bomToolResult.evaluatedBomTools);

            if (isOnline) {
                Optional<ProjectVersionView> projectView = detectProjectService.createOrUpdateHubProject(bomToolProjectInfo);
                if (projectView.isPresent() && unmapCodeLocations) {
                    detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
                }
            }

            BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, bomToolProjectInfo, bomToolResult.bomToolCodeLocations);
            if (bdioResult.getBdioFiles().size() > 0) {
                if (isOnline) {
                    detectBdioUploadService.uploadBdioFiles(bdioResult.getBdioFiles());
                } else {
                    /*
                       for (final File bdio : detectProject.getBdioFiles()) {
                            diagnosticManager.registerGlobalFileOfInterest(bdio);
                        }
                     */
                }
            } else {
                logger.debug("Did not create any bdio files.");
            }
        }

        
        /*
        if (detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE)) {
            for (final File bdio : detectProject.getBdioFiles()) {
                diagnosticManager.registerGlobalFileOfInterest(bdio);
            }
        } else {
            final Optional<ProjectVersionView> originalProjectVersionView = hubManager.updateHubProjectVersion(detectProject);
            ProjectVersionView projectVersionView = null;
            if (originalProjectVersionView.isPresent()) {
                projectVersionView = originalProjectVersionView.get();
            }
            hubManager.performScanActions(detectProject);
            hubManager.performBinaryScanActions(detectProject);
            // final ProjectVersionView projectVersionView = originalProjectVersionView.orElse(scanProjectVersionView.orElse(null));
            hubManager.performPostHubActions(detectProject, projectVersionView);
        }

        */

    }

}
