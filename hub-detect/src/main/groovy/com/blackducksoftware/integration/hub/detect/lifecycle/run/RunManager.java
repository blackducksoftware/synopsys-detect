package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
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
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OfflineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OnlineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.ScanJobManagerFactory;
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
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(RunManager.class);

    private final RunDependencies runDependencies;

    public RunManager(final RunDependencies runDependencies) {
        this.runDependencies = runDependencies;
    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        runDependencies.phoneHomeManager.startPhoneHome();

        DetectConfiguration detectConfiguration = runDependencies.detectConfiguration;
        DirectoryManager directoryManager = runDependencies.directoryManager;
        EventSystem eventSystem = runDependencies.eventSystem;

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

        DetectFileFinder detectFileFinder = new DetectFileFinder();
        ConnectionManager connectionManager = new ConnectionManager(detectConfiguration);
        if (bomToolsEnabled) {
            logger.info("Locating bom tool dependencies.");
            final BomToolDependencies bomToolDependencies = new BomToolDependencies();
            bomToolDependencies.gson = runDependencies.gson;
            bomToolDependencies.configuration = runDependencies.configuration;
            bomToolDependencies.documentBuilder = runDependencies.documentBuilder;
            bomToolDependencies.executableRunner = new ExecutableRunner();
            AirGapOptions airGapOptions = detectConfigurationFactory.createAirGapOptions();
            bomToolDependencies.airGapManager = new AirGapManager(airGapOptions);
            bomToolDependencies.executableManager = new ExecutableManager(new DetectFileFinder(), runDependencies.detectInfo);
            bomToolDependencies.externalIdFactory = new ExternalIdFactory();
            bomToolDependencies.detectFileFinder = detectFileFinder;
            bomToolDependencies.directoryManager = runDependencies.directoryManager;
            bomToolDependencies.detectConfiguration = detectConfiguration;
            bomToolDependencies.connectionManager = connectionManager;
            bomToolDependencies.standardExecutableFinder = new StandardExecutableFinder(directoryManager, bomToolDependencies.executableManager, detectConfiguration);

            logger.info("Preparing to initialize bom tools.");
            AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext();
            runContext.setDisplayName("Detect Bom Tools " + runDependencies.detectRun.getRunId());
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

            if (isOnline && runDependencies.hubServiceManager != null) {
                HubServiceManager hubServiceManager = runDependencies.hubServiceManager;
                DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
                DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager, options);
                projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
                if (projectView.isPresent() && unmapCodeLocations) {
                    DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.createHubService(), hubServiceManager.createCodeLocationService());
                    detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
                }
            }

            BdioManager bdioManager = new BdioManager(runDependencies.detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, detectCodeLocationManager, directoryManager);
            BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, projectNameVersion.get(), bomToolsResult.bomToolCodeLocations);
            if (bdioResult.getBdioFiles().size() > 0) {
                if (isOnline) {
                    HubServiceManager hubServiceManager = runDependencies.hubServiceManager;
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
                HubServiceManager hubServiceManager = runDependencies.hubServiceManager;
                DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
                DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager, options);
                projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion.get());
            }

        }

        if (sigScanEnabled) {
            final String localScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None);
            final String userProvidedScannerInstallUrl = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None);
            Integer parrallelProcessors = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, PropertyAuthority.None);
            Integer timeout = detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT, PropertyAuthority.None);
            Boolean trustCert = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_TRUST_CERT, PropertyAuthority.None);

            final ExecutorService executorService = Executors.newFixedThreadPool(parrallelProcessors);
            IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

            BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();

            ScanJobManagerFactory scanJobManagerFactory = new ScanJobManagerFactory();
            ScanJobManager scanJobManager;
            if (isOnline && StringUtils.isBlank(userProvidedScannerInstallUrl) && StringUtils.isBlank(localScannerInstallPath)) {
                // will will use the hub server to download/update the scanner - this is the most likely situation
                HubServerConfig hubServerConfig = runDependencies.hubServiceManager.getHubServerConfig();
                scanJobManager = scanJobManagerFactory.withHubInstall(hubServerConfig, executorService, intEnvironmentVariables);
            } else {
                if (StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
                    // we will use the provided url to download/update the scanner
                    scanJobManager = scanJobManagerFactory.withUserProvidedUrl(userProvidedScannerInstallUrl, connectionManager, timeout, trustCert, executorService, intEnvironmentVariables);
                } else {
                    // either we were given an existing path for the scanner or
                    // we are offline - either way, we won't attempt to manage the install
                    scanJobManager = scanJobManagerFactory.withoutInstall(executorService, intEnvironmentVariables);
                }
            }

            BlackDuckSignatureScanner blackDuckSignatureScanner;
            if (isOnline) {
                HubServerConfig hubServerConfig = runDependencies.hubServiceManager.getHubServerConfig();
                blackDuckSignatureScanner = new OnlineBlackDuckSignatureScanner(directoryManager, detectFileFinder, codeLocationNameManager, blackDuckSignatureScannerOptions, eventSystem, scanJobManager, hubServerConfig);
            } else {
                blackDuckSignatureScanner = new OfflineBlackDuckSignatureScanner(directoryManager, detectFileFinder, codeLocationNameManager, blackDuckSignatureScannerOptions, eventSystem, scanJobManager);
            }
            try {
                blackDuckSignatureScanner.performScanActions(projectNameVersion.get(), null); //TODO: get docker tar file.
            } catch (IOException e) {
                logger.info("Signature scan failed!");
            } finally {
                executorService.shutdownNow();
            }
        }
    }
}