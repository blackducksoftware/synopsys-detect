package com.synopsys.integration.detect.lifecycle.run.operation;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.developermode.DeveloperScanService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidMode;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidModePostActions;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class RapidScanOperation extends BlackDuckOperation {
    private final Gson gson;

    public RapidScanOperation(DetectContext detectContext, DetectInfo detectInfo,
        ProductRunData productRunData, DirectoryManager directoryManager, EventSystem eventSystem,
        DetectConfigurationFactory detectConfigurationFactory, DetectToolFilter detectToolFilter,
        CodeLocationNameManager codeLocationNameManager, BdioCodeLocationCreator bdioCodeLocationCreator,
        RunOptions runOptions, boolean priorStepsSucceeded, Gson gson) {
        super(detectContext, detectInfo, productRunData, directoryManager, eventSystem, detectConfigurationFactory, detectToolFilter, codeLocationNameManager, bdioCodeLocationCreator, runOptions, priorStepsSucceeded);
        this.gson = gson;
    }

    @Override
    public String getOperationName() {
        return "Black Duck (Full Scan)";
    }

    @Override
    protected OperationResult<RunResult> executeOperation(RunResult input) throws DetectUserFriendlyException, IntegrationException {
        NameVersion projectNameVersion = getProjectInformation(input);
        AggregateOptions aggregateOptions = determineAggregationStrategy(getRunOptions(), !havePriorStepsSucceeded());
        BlackDuckRunData blackDuckRunData = getProductRunData().getBlackDuckRunData();

        blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);
        BdioResult bdioResult = createBdioFiles(input, aggregateOptions, projectNameVersion);

        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        DeveloperScanService developerScanService = blackDuckServicesFactory.createDeveloperScanService();
        BlackDuckRapidMode developerMode = new BlackDuckRapidMode(blackDuckRunData, developerScanService, getDetectConfigurationFactory());
        List<DeveloperScanComponentResultView> results = developerMode.run(bdioResult);
        BlackDuckRapidModePostActions postActions = new BlackDuckRapidModePostActions(gson, getEventSystem(), getDirectoryManager());
        postActions.perform(projectNameVersion, results);
        return OperationResult.success(input);
    }
}
