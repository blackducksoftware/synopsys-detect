package com.blackducksoftware.integration.hub.detect.workflow.run;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.exit.ExitCodeManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectorStatus;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.log.Slf4jIntLogger;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(RunManager.class);

    private final PhoneHomeManager phoneHomeManager;
    private final DetectConfiguration detectConfiguration;
    private final BomToolManager bomToolManager;
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final EventSystem eventSystem;

    public RunManager(final PhoneHomeManager phoneHomeManager, final DetectConfiguration detectConfiguration, final BomToolManager bomToolManager,
        final DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager, EventSystem eventSystem) {

        this.phoneHomeManager = phoneHomeManager;
        this.detectConfiguration = detectConfiguration;

        this.bomToolManager = bomToolManager;
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.eventSystem = eventSystem;

    }

    public void run() {
        phoneHomeManager.startPhoneHome();

        if (!this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED)) {
            BomToolResult result = bomToolManager.runBomTools();

            Map<BomToolGroupType, Result> bomToolResults = new HashMap<>();
            result.succesfullBomToolGroupTypes.forEach(it -> bomToolResults.put(it, Result.SUCCESS));
            result.failedBomToolGroupTypes.forEach(it -> bomToolResults.put(it, Result.FAILURE));

            bomToolResults.forEach((bomTool, bomToolResult) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(bomTool, bomToolResult == Result.SUCCESS ? StatusType.SUCCESS : StatusType.FAILURE)));
        }

        
        /*
        AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext(DetectSharedBeanConfiguration.class, DetectBootBeanConfiguration.class);

        applicationContext.getBeanFactory().registerSingleton("", "");

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

        if (status.failedBomToolGroupTypes.size() > 0){
            return ExitCodeType.FAILURE_BOM_TOOL;
        }
        if (status.missingBomToolGroupTypes.size() > 0){
            return ExitCodeType.FAILURE_BOM_TOOL_REQUIRED;
        }
        */

        /* cleanup
        try {
            if (bootResult != null) {
                CleanupManager cleanupManager = new CleanupManager();
                cleanupManager.cleanup(bootResult.detectRunDependencies);
            }
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }
 */
        boolean printOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT);
        if (!printOutput) {
            detectStatusManager.logDetectResults(new Slf4jIntLogger(logger), exitCodeManager.getWinningExitCode());
            //detectSummaryManager.logDetectResults(, currentExitCodeType);
        }

    }

}
