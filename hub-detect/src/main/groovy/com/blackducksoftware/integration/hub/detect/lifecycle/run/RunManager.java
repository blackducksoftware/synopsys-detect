package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.blackducksoftware.integration.hub.detect.BomToolBeanConfiguration;
import com.blackducksoftware.integration.hub.detect.BomToolDependencies;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.boot.DetectRunDependencies;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapOptions;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckBinaryScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(RunManager.class);

    private final DetectRunDependencies detectRunDependencies;

    public RunManager(final DetectRunDependencies detectRunDependencies) {
        this.detectRunDependencies = detectRunDependencies;
    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        detectRunDependencies.phoneHomeManager.startPhoneHome();

        DetectConfiguration detectConfiguration = detectRunDependencies.detectConfiguration;
        DirectoryManager directoryManager = detectRunDependencies.directoryManager;
        EventSystem eventSystem = detectRunDependencies.eventSystem;

        boolean bomToolsEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED, PropertyAuthority.None);
        boolean sigScanEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None);
        boolean isOnline = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);

        Optional<NameVersion> projectNameVersion = Optional.empty();
        Optional<ProjectVersionView> projectView = Optional.empty();

        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration);

        BomToolNameVersionDecider bomToolNameVersionDecider = new BomToolNameVersionDecider();
        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionManager projectNameVersionManager = new ProjectNameVersionManager(projectNameVersionOptions, bomToolNameVersionDecider);

        CodeLocationNameService codeLocationNameService = new CodeLocationNameService(new DetectFileFinder());
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(detectConfiguration, codeLocationNameService);
        DetectCodeLocationManager detectCodeLocationManager = new DetectCodeLocationManager(codeLocationNameManager, detectConfiguration, directoryManager, eventSystem);

        if (bomToolsEnabled) {
            logger.info("Locating bom tool dependencies.");
            final BomToolDependencies bomToolDependencies = new BomToolDependencies();
            bomToolDependencies.gson = detectRunDependencies.gson;
            bomToolDependencies.configuration = detectRunDependencies.configuration;
            bomToolDependencies.documentBuilder = detectRunDependencies.documentBuilder;
            bomToolDependencies.executableRunner = new ExecutableRunner();
            AirGapOptions airGapOptions = detectConfigurationFactory.createAirGapOptions();
            bomToolDependencies.airGapManager = new AirGapManager(airGapOptions);
            bomToolDependencies.executableManager = new ExecutableManager(new DetectFileFinder(), detectRunDependencies.detectInfo);
            bomToolDependencies.externalIdFactory = new ExternalIdFactory();
            bomToolDependencies.detectFileFinder = new DetectFileFinder();
            bomToolDependencies.directoryManager = detectRunDependencies.directoryManager;
            bomToolDependencies.detectConfiguration = detectConfiguration;
            bomToolDependencies.connectionManager = new ConnectionManager(detectConfiguration);
            bomToolDependencies.standardExecutableFinder = new StandardExecutableFinder(directoryManager, bomToolDependencies.executableManager, detectConfiguration);

            logger.info("Preparing to initialize bom tools.");
            AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext();
            runContext.setDisplayName("Detect Bom Tools " + detectRunDependencies.detectRun.getRunId());
            runContext.register(BomToolBeanConfiguration.class);
            runContext.registerBean(BomToolDependencies.class, () -> bomToolDependencies);
            runContext.refresh();
            logger.info("Bom tools initialized. Retrieving bom tool factory.");
            BomToolFactory bomToolFactory = runContext.getBean(BomToolFactory.class);

            logger.info("Building bom tool system.");
            SearchOptions searchOptions = detectConfigurationFactory.createSearchOptions(directoryManager.getSourceDirectory());
            BomToolSearchProvider bomToolSearchProvider = new BomToolSearchProvider(bomToolFactory);
            BomToolSearchEvaluator bomToolSearchEvaluator = new BomToolSearchEvaluator();

            SearchManager searchManager = new SearchManager(searchOptions, bomToolSearchProvider, bomToolSearchEvaluator, eventSystem);
            PreparationManager preparationManager = new PreparationManager(eventSystem);
            ExtractionManager extractionManager = new ExtractionManager();

            BomToolManager bomToolManager = new BomToolManager(searchManager, extractionManager, preparationManager, eventSystem);
            logger.info("Running bom tools.");
            BomToolsResult bomToolsResult = bomToolManager.runBomTools();
            logger.info("Finished running bom tools.");

            projectNameVersion = Optional.of(projectNameVersionManager.evaluateProjectNameVersion(bomToolsResult.evaluatedBomTools));

            if (isOnline && detectRunDependencies.hubServiceManager != null) {
                HubServiceManager hubServiceManager = detectRunDependencies.hubServiceManager;
                DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
                DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager, options);
                projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
                if (projectView.isPresent() && unmapCodeLocations) {
                    DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.createHubService(), hubServiceManager.createCodeLocationService());
                    detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
                }
            }

            BdioManager bdioManager = new BdioManager(detectRunDependencies.detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, detectCodeLocationManager, directoryManager);
            BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, projectNameVersion.get(), bomToolsResult.bomToolCodeLocations);
            if (bdioResult.getBdioFiles().size() > 0) {
                if (isOnline) {
                    HubServiceManager hubServiceManager = detectRunDependencies.hubServiceManager;
                    DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(detectConfiguration, eventSystem, hubServiceManager.createCodeLocationService());
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
            if (isOnline) {
                HubServiceManager hubServiceManager = detectRunDependencies.hubServiceManager;
                DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
                DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager, options);
                projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
            }

        }

        if (isOnline) {
            HubServiceManager hubServiceManager = detectRunDependencies.hubServiceManager;
            BlackDuckBinaryScanner blackDuckBinaryScanner = new BlackDuckBinaryScanner(codeLocationNameService, detectConfiguration, hubServiceManager);
            blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion.get());

            BlackDuckSignatureScanner blackDuckSignatureScanner = new BlackDuckSignatureScanner(directoryManager, new DetectFileFinder(), codeLocationNameManager, detectConfiguration, eventSystem, hubServiceManager);
            blackDuckSignatureScanner.performScanActions(projectNameVersion.get());

            if (projectView.isPresent()) {
                PolicyChecker policyChecker = new PolicyChecker(detectConfiguration);
                HubManager hubManager = new HubManager(codeLocationNameManager, detectConfiguration, hubServiceManager, policyChecker);
                hubManager.performPostHubActions(projectNameVersion.get(), projectView.get());
            }

            //TODO: replicate bdio file list check (if bdioFiles.exist && scanWasRan)
            HubService hubService = hubServiceManager.createHubService();
            final String componentsLink = hubService.getFirstLinkSafely(projectView.get(), ProjectVersionView.COMPONENTS_LINK);
            logger.info(String.format("To see your results, follow the URL: %s", componentsLink));

        } else {
            HubServiceManager hubServiceManager = detectRunDependencies.hubServiceManager;
            BlackDuckBinaryScanner blackDuckBinaryScanner = new BlackDuckBinaryScanner(codeLocationNameService, detectConfiguration, hubServiceManager);
            blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion.get());
        }
    }
}
