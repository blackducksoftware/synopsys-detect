package com.blackducksoftware.integration.hub.detect.workflow.run;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.synopsys.integration.blackduck.summary.Result;

public class RunManager {

    private final PhoneHomeManager phoneHomeManager;
    private final DetectConfiguration detectConfiguration;
    private final BomToolManager bomToolManager;

    public RunManager(final PhoneHomeManager phoneHomeManager, final DetectConfiguration detectConfiguration, final BomToolManager bomToolManager) {

        this.phoneHomeManager = phoneHomeManager;
        this.detectConfiguration = detectConfiguration;

        this.bomToolManager = bomToolManager;
    }

    public void run() {
        phoneHomeManager.startPhoneHome();

        if (!this.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED)) {
            BomToolResult result = bomToolManager.runBomTools();

            Map<BomToolGroupType, Result> bomToolResults = new HashMap<>();
            result.succesfullBomToolGroupTypes.forEach(it -> bomToolResults.put(it, Result.SUCCESS));
            result.failedBomToolGroupTypes.forEach(it -> bomToolResults.put(it, Result.FAILURE));
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

        if (result.failedBomToolGroupTypes.size() > 0){
            return ExitCodeType.FAILURE_BOM_TOOL;
        }
        if (result.missingBomToolGroupTypes.size() > 0){
            return ExitCodeType.FAILURE_BOM_TOOL_REQUIRED;
        }
        */
    }

}
