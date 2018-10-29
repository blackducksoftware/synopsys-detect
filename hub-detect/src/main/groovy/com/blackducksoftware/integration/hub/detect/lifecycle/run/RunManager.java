package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.BomToolDependenciesBuilder;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorTool;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerOptions;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerProperties;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerResult;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerTool;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OfflineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OnlineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.ScanJobManagerFactory;
import com.blackducksoftware.integration.hub.detect.tool.swip.SwipCliManager;
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapOptions;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckBinaryScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolEvaluationNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectToolProjectInfo;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.bdio.BdioTransformer;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RunDependencies runDependencies;

    public RunManager(final RunDependencies runDependencies) {
        this.runDependencies = runDependencies;
    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        runDependencies.phoneHomeManager.startPhoneHome();

        DetectConfiguration detectConfiguration = runDependencies.detectConfiguration;
        DirectoryManager directoryManager = runDependencies.directoryManager;
        EventSystem eventSystem = runDependencies.eventSystem;
        DetectRun detectRun = runDependencies.detectRun;
        DetectInfo detectInfo = runDependencies.detectInfo;

        ReportManager.createDefault(eventSystem);

        //this should be detect run options
        boolean bomToolsEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED, PropertyAuthority.None);
        boolean sigScanEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None);
        boolean binScanEnabled = StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None));
        boolean isOnline = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        boolean swipEnabled = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SWIP_ENABLED, PropertyAuthority.None);
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);
        String preferredTools = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_TOOL, PropertyAuthority.None);

        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration);

        DetectFileFinder detectFileFinder = new DetectFileFinder();
        ConnectionManager connectionManager = new ConnectionManager(detectConfiguration);

        CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(detectConfiguration, codeLocationNameService);
        DetectCodeLocationManager detectCodeLocationManager = new DetectCodeLocationManager(codeLocationNameManager, detectConfiguration, directoryManager, eventSystem);

        List<DetectToolProjectInfo> detectToolProjectInfo = new ArrayList<>();
        List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();

        ExecutableRunner exectableRunner = new ExecutableRunner();
        ExecutableManager executableManager = new ExecutableManager(detectFileFinder, detectInfo);
        StandardExecutableFinder standardExecutableFinder = new StandardExecutableFinder(directoryManager, executableManager, detectConfiguration);

        AirGapOptions airGapOptions = detectConfigurationFactory.createAirGapOptions();
        AirGapManager airGapManager = new AirGapManager(airGapOptions);
        MavenMetadataService mavenMetadataService = new MavenMetadataService(runDependencies.documentBuilder, connectionManager);
        DockerInspectorManager dockerInspectorManager = new DockerInspectorManager(directoryManager, airGapManager, detectFileFinder, detectConfiguration, connectionManager, mavenMetadataService);

        DockerOptions dockerOptions = DockerOptions.fromConfiguration(detectConfiguration);
        Optional<File> dockerTar = Optional.empty();
        DockerProperties dockerProperties = new DockerProperties(detectConfiguration);
        BdioTransformer bdioTransformer = new BdioTransformer();
        DockerExtractor dockerExtractor = new DockerExtractor(detectFileFinder, directoryManager, dockerProperties, exectableRunner, bdioTransformer, new ExternalIdFactory(), runDependencies.gson);
        DockerTool dockerTool = new DockerTool(dockerInspectorManager, standardExecutableFinder, dockerExtractor, dockerOptions);
        if (dockerTool.shouldRun()) {
            logger.info("Will run the docker tool.");
            DockerResult dockerResult = dockerTool.run(directoryManager.getSourceDirectory(), directoryManager.getDockerOutputDirectory());
            Extraction dockerExtraction = dockerResult.getExtraction();
            dockerTar = Optional.ofNullable(dockerResult.getDockerTarFile());
            DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(DetectTool.DOCKER, new NameVersion(dockerExtraction.projectName, dockerExtraction.projectVersion));
            detectToolProjectInfo.add(dockerProjectInfo);
        }

        if (bomToolsEnabled) {
            logger.info("Will run the detector tool.");
            String projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_BOM_TOOL, PropertyAuthority.None);
            final BomToolDependenciesBuilder bomToolDependenciesBuilder = new BomToolDependenciesBuilder();
            bomToolDependenciesBuilder.fromRunDependencies(runDependencies).setConnectionManager(connectionManager);
            bomToolDependenciesBuilder.setAirGapManager(airGapManager).setDetectFileFinder(detectFileFinder).setConfiguration(runDependencies.configuration);
            bomToolDependenciesBuilder.fromDefaults().setExecutableRunner(exectableRunner).setExecutableManager(executableManager).setStandardExecutableFinder(standardExecutableFinder);

            DetectorTool detectorTool = new DetectorTool(detectRun, bomToolDependenciesBuilder.build(), eventSystem);
            SearchOptions searchOptions = detectConfigurationFactory.createSearchOptions(directoryManager.getSourceDirectory());

            BomToolsResult bomToolsResult = detectorTool.performBomTools(searchOptions);

            BomToolEvaluationNameVersionDecider bomToolEvaluationNameVersionDecider = new BomToolEvaluationNameVersionDecider(new BomToolNameVersionDecider());
            Optional<NameVersion> bomToolNameVersion = bomToolEvaluationNameVersionDecider.decideSuggestion(bomToolsResult.evaluatedBomTools, projectBomTool);
            if (bomToolNameVersion.isPresent()) {
                DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(DetectTool.DOCKER, new NameVersion(bomToolNameVersion.get().getName(), bomToolNameVersion.get().getVersion()));
                detectToolProjectInfo.add(dockerProjectInfo);
            }

            detectCodeLocations.addAll(bomToolsResult.bomToolCodeLocations);
        }

        logger.info("Completed code location tools.");

        logger.info("Determining project info.");

        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        NameVersion projectNameVersion = projectNameVersionDecider.decideProjectNameVersion(preferredTools, detectToolProjectInfo);

        logger.info("Project name: " + projectNameVersion.getName());
        logger.info("Project version: " + projectNameVersion.getVersion());

        Optional<HubServiceManager> hubServiceManager = Optional.ofNullable(runDependencies.hubServiceManager);

        Optional<ProjectVersionView> projectView = Optional.empty();

        if (isOnline && hubServiceManager.isPresent()) {
            logger.info("Getting or creating project.");
            DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
            DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager.get(), options);
            projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion);
            if (projectView.isPresent() && unmapCodeLocations) {
                logger.info("Unmapping code locations.");
                DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.get().createHubService(), hubServiceManager.get().createCodeLocationService());
                detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
            }
        }

        logger.info("Generating BDIO files.");
        BdioManager bdioManager = new BdioManager(runDependencies.detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, detectCodeLocationManager, directoryManager);
        BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, projectNameVersion, detectCodeLocations);

        if (bdioResult.getBdioFiles().size() > 0) {
            bdioResult.getBdioFiles().forEach(it -> eventSystem.publishEvent(Event.OutputFileOfInterest, it));
            if (isOnline && hubServiceManager.isPresent()) {
                logger.info("Uploading BDIO files.");
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(detectConfiguration, hubServiceManager.get().createCodeLocationService());
                detectBdioUploadService.uploadBdioFiles(bdioResult.getBdioFiles());
            }
        } else {
            logger.debug("Did not create any bdio files.");
        }

        logger.info("Completed bdio.");

        if (sigScanEnabled) {
            logger.info("Will run the signature scanner tool.");
            final String localScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None);
            final String userProvidedScannerInstallUrl = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None);

            BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
            final ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParrallelProcessors());
            IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

            ScanJobManagerFactory scanJobManagerFactory = new ScanJobManagerFactory();
            ScanJobManager scanJobManager;
            if (isOnline && StringUtils.isBlank(userProvidedScannerInstallUrl) && StringUtils.isBlank(localScannerInstallPath)) {
                // will will use the hub server to download/update the scanner - this is the most likely situation
                HubServerConfig hubServerConfig = runDependencies.hubServiceManager.getHubServerConfig();
                scanJobManager = scanJobManagerFactory.withHubInstall(hubServerConfig, executorService, intEnvironmentVariables);
            } else {
                if (StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
                    // we will use the provided url to download/update the scanner
                    scanJobManager = scanJobManagerFactory.withUserProvidedUrl(userProvidedScannerInstallUrl, connectionManager, executorService, intEnvironmentVariables);
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
                blackDuckSignatureScanner.performScanActions(projectNameVersion, dockerTar.orElse(null)); //TODO: get docker tar file.
            } catch (IOException e) {
                logger.info("Signature scan failed!");
            } finally {
                executorService.shutdownNow();
            }
        }

        if (binScanEnabled && hubServiceManager.isPresent()) {
            logger.info("Will run the binary scanner tool.");
            BlackDuckBinaryScanner blackDuckBinaryScanner = new BlackDuckBinaryScanner(codeLocationNameService, detectConfiguration, hubServiceManager.get());
            blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion);
        }

        if (swipEnabled) {
            SwipCliManager swipCliManager = new SwipCliManager(directoryManager, new ExecutableRunner(), connectionManager);
            logger.info("Will run the swip tool.");
            swipCliManager.runSwip(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
        }

        if (isOnline && hubServiceManager.isPresent() && projectView.isPresent()) {
            HubManager hubManager = new HubManager(codeLocationNameManager, detectConfiguration, hubServiceManager.get(), new PolicyChecker(detectConfiguration));
            hubManager.performPostHubActions(projectNameVersion, projectView.get());
        }
    }
}