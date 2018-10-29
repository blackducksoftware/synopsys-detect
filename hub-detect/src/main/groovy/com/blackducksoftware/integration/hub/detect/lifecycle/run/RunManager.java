package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.BomToolDependenciesBuilder;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorTool;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OfflineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.OnlineBlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.ScanJobManagerFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
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

        //this should be detect run options
        boolean bomToolsEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOLS_DISABLED, PropertyAuthority.None);
        boolean sigScanEnabled = !detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None);
        boolean isOnline = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);

        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration);

        BomToolNameVersionDecider bomToolNameVersionDecider = new BomToolNameVersionDecider();
        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionManager projectNameVersionManager = new ProjectNameVersionManager(projectNameVersionOptions, bomToolNameVersionDecider);

        DetectFileFinder detectFileFinder = new DetectFileFinder();
        ConnectionManager connectionManager = new ConnectionManager(detectConfiguration);

        CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(detectConfiguration, codeLocationNameService);
        DetectCodeLocationManager detectCodeLocationManager = new DetectCodeLocationManager(codeLocationNameManager, detectConfiguration, directoryManager, eventSystem);

        Optional<HubServiceManager> hubServiceManager = Optional.ofNullable(runDependencies.hubServiceManager);
        Optional<DetectProjectService> projectService = Optional.empty();
        Optional<NameVersion> projectNameVersion = Optional.empty();
        Optional<ProjectVersionView> projectView = Optional.empty();

        if (hubServiceManager.isPresent()) {
            DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
            DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager.get(), options);
            projectService = Optional.of(detectProjectService);
        }

        if (bomToolsEnabled) {
            final BomToolDependenciesBuilder bomToolDependenciesBuilder = new BomToolDependenciesBuilder();
            bomToolDependenciesBuilder.fromRunDependencies(runDependencies).setConnectionManager(connectionManager);
            bomToolDependenciesBuilder.fromDetectConfigurationFactory(detectConfigurationFactory);
            bomToolDependenciesBuilder.fromDefaults(detectInfo);
            DetectorTool detectorTool = new DetectorTool(detectRun, bomToolDependenciesBuilder.build(), eventSystem);
            SearchOptions searchOptions = detectConfigurationFactory.createSearchOptions(directoryManager.getSourceDirectory());

            BomToolsResult bomToolsResult = detectorTool.performBomTools(searchOptions);
            projectNameVersion = Optional.of(projectNameVersionManager.evaluateProjectNameVersion(bomToolsResult.evaluatedBomTools));

            if (isOnline && hubServiceManager.isPresent() && projectService.isPresent()) {
                projectView = projectService.get().createOrUpdateHubProject(projectNameVersion.get());
                if (projectView.isPresent() && unmapCodeLocations) {
                    DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.get().createHubService(), hubServiceManager.get().createCodeLocationService());
                    detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
                }
            }

            BdioManager bdioManager = new BdioManager(runDependencies.detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, detectCodeLocationManager, directoryManager);
            BdioResult bdioResult = bdioManager.createBdioFiles(aggregateName, projectNameVersion.get(), bomToolsResult.bomToolCodeLocations);
            if (bdioResult.getBdioFiles().size() > 0) {
                bdioResult.getBdioFiles().forEach(it -> eventSystem.publishEvent(Event.OutputFileOfInterest, it));
                if (isOnline && hubServiceManager.isPresent()) {
                    DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(detectConfiguration, hubServiceManager.get().createCodeLocationService());
                    detectBdioUploadService.uploadBdioFiles(bdioResult.getBdioFiles());
                }
            } else {
                logger.debug("Did not create any bdio files.");
            }
        }

        if (!projectNameVersion.isPresent()) {
            projectNameVersion = Optional.of(projectNameVersionManager.evaluateDefaultProjectNameVersion());
            if (isOnline && projectService.isPresent()) {
                projectView = projectService.get().createOrUpdateHubProject(projectNameVersion.get());
            }

        }

        if (sigScanEnabled) {
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
                blackDuckSignatureScanner.performScanActions(projectNameVersion.get(), null); //TODO: get docker tar file.
            } catch (IOException e) {
                logger.info("Signature scan failed!");
            } finally {
                executorService.shutdownNow();
            }
        }
    }
}