package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorTool;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorToolResult;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerOptions;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerTool;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerToolResult;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.blackducksoftware.integration.hub.detect.tool.swip.SwipCliManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckBinaryScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public RunManager(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public void run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        //TODO: Better way for run manager to get dependencies so he can be tested. (And better ways of creating his objects)
        PhoneHomeManager phoneHomeManager = detectContext.getBean(PhoneHomeManager.class);
        DetectConfiguration detectConfiguration = detectContext.getBean(DetectConfiguration.class);
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);
        CodeLocationNameManager codeLocationNameManager = detectContext.getBean(CodeLocationNameManager.class);
        BdioCodeLocationCreator bdioCodeLocationCreator = detectContext.getBean(BdioCodeLocationCreator.class);
        ConnectionManager connectionManager = detectContext.getBean(ConnectionManager.class);
        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);
        Optional<HubServiceManager> hubServiceManager = Optional.ofNullable(detectContext.getBean(HubServiceManager.class));

        phoneHomeManager.startPhoneHome();

        RunResult runResult = new RunResult();
        RunOptions runOptions = detectConfigurationFactory.createRunOptions();

        DockerOptions dockerOptions = DockerOptions.fromConfiguration(detectConfiguration);
        if (dockerOptions.hasDockerImageOrTag()) {
            logger.info("Will run the docker tool.");
            DockerTool dockerTool = new DockerTool(detectContext);

            DockerToolResult dockerToolResult = dockerTool.run();
            runResult.addToolNameVersionIfPresent(DetectTool.DOCKER, dockerToolResult.dockerProjectNameVersion);
            runResult.addDetectCodeLocations(dockerToolResult.dockerCodeLocations);
            runResult.addDockerFile(dockerToolResult.dockerTar);
            logger.info("Docker tool has finished.");
        }

        if (runOptions.isBomToolsEnabled()) {
            logger.info("Will run the detector tool.");
            String projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_BOM_TOOL, PropertyAuthority.None);
            SearchOptions searchOptions = detectConfigurationFactory.createSearchOptions(directoryManager.getSourceDirectory());
            DetectorTool detectorTool = new DetectorTool(detectContext);

            DetectorToolResult detectorToolResult = detectorTool.performBomTools(searchOptions, projectBomTool);
            runResult.addToolNameVersionIfPresent(DetectTool.DETECTOR, detectorToolResult.bomToolProjectNameVersion);
            runResult.addDetectCodeLocations(detectorToolResult.bomToolCodeLocations);
            logger.info("Detector tool has finished.");
        }

        logger.info("Completed code location tools.");

        logger.info("Determining project info.");

        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        NameVersion projectNameVersion = projectNameVersionDecider.decideProjectNameVersion(runOptions.getPreferredTools(), runResult.getDetectToolProjectInfo());

        logger.info("Project name: " + projectNameVersion.getName());
        logger.info("Project version: " + projectNameVersion.getVersion());

        Optional<ProjectVersionView> projectView = Optional.empty();

        if (runOptions.isOnline() && hubServiceManager.isPresent()) {
            logger.info("Getting or creating project.");
            DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
            DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager.get(), options);
            projectView = detectProjectService.createOrUpdateHubProject(projectNameVersion);
            if (projectView.isPresent() && runOptions.isUnmapCodeLocations()) {
                logger.info("Unmapping code locations.");
                DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.get().createHubService(), hubServiceManager.get().createCodeLocationService());
                detectCodeLocationUnmapService.unmapCodeLocations(projectView.get());
            }
        }

        logger.info("Completed project and version actions.");

        logger.info("Processing Detect Code Locations.");
        BdioManager bdioManager = new BdioManager(detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, bdioCodeLocationCreator, directoryManager);
        BdioResult bdioResult = bdioManager.createBdioFiles(runOptions.getAggregateName(), projectNameVersion, runResult.getDetectCodeLocations());

        if (bdioResult.getBdioFiles().size() > 0) {
            logger.info("Created " + bdioResult.getBdioFiles().size() + " BDIO files.");
            bdioResult.getBdioFiles().forEach(it -> eventSystem.publishEvent(Event.OutputFileOfInterest, it));
            if (runOptions.isOnline() && hubServiceManager.isPresent()) {
                logger.info("Uploading BDIO files.");
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(detectConfiguration, hubServiceManager.get().createCodeLocationService());
                detectBdioUploadService.uploadBdioFiles(bdioResult.getBdioFiles());
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }

        logger.info("Completed Detect Code Location processing.");

        if (runOptions.isSigScanEnabled()) {
            logger.info("Will run the signature scanner tool.");
            BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
            BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, detectContext);
            blackDuckSignatureScannerTool.runScanTool(projectNameVersion, runResult.getDockerTar());
            logger.info("Signature scanner tool has finished.");
        }

        if (runOptions.isBinScanEnabled() && hubServiceManager.isPresent()) {
            logger.info("Will run the binary scanner tool.");
            BlackDuckBinaryScanner blackDuckBinaryScanner = new BlackDuckBinaryScanner(codeLocationNameManager, detectConfiguration, hubServiceManager.get());
            blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion);
            logger.info("Binary scanner tool has finished.");
        }

        if (runOptions.isSwipEnabled()) {
            logger.info("Will run the swip tool.");
            SwipCliManager swipCliManager = new SwipCliManager(directoryManager, new ExecutableRunner(), connectionManager);
            swipCliManager.runSwip(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
            logger.info("Swip tool has finished.");
        }

        if (runOptions.isOnline() && hubServiceManager.isPresent() && projectView.isPresent()) {
            logger.info("Will perform Black Duck actions.");
            HubManager hubManager = new HubManager(codeLocationNameManager, detectConfiguration, hubServiceManager.get(), new PolicyChecker(detectConfiguration));
            hubManager.performPostHubActions(projectNameVersion, projectView.get());
            logger.info("Black Duck actions have finished.");
        }

        logger.info("All tools have finished.");
    }
}