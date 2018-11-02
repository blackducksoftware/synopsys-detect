package com.blackducksoftware.integration.hub.detect.lifecycle.shutdown;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.RequiredDetectorChecker;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final PhoneHomeManager phoneHomeManager;
    private final DirectoryManager directoryManager;
    private final DetectConfiguration detectConfiguration;
    private final ReportManager reportManager;
    private final DiagnosticManager diagnosticManager;

    public ShutdownManager(DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager,
        final PhoneHomeManager phoneHomeManager, final DirectoryManager directoryManager, final DetectConfiguration detectConfiguration, ReportManager reportManager, DiagnosticManager diagnosticManager) {
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.phoneHomeManager = phoneHomeManager;
        this.directoryManager = directoryManager;
        this.detectConfiguration = detectConfiguration;
        this.reportManager = reportManager;
        this.diagnosticManager = diagnosticManager;
    }

    public ExitCodeType shutdown(Optional<RunResult> runResultOptional) {
        try {
            logger.debug("Ending phone home.");
            phoneHomeManager.endPhoneHome();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
        }

        try {
            logger.debug("Ending diagnostics.");
            diagnosticManager.finish();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to finish diagnostics: %s", e.getMessage()));
        }

        try {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None)) {
                logger.info("Cleaning up directory: " + directoryManager.getRunHomeDirectory().getAbsolutePath());
                FileUtils.deleteDirectory(directoryManager.getRunHomeDirectory());
            } else {
                logger.info("Skipping cleanup, it is disabled.");
            }
        } catch (final Exception e) {
            logger.debug(String.format("Error trying cleanup the run directory: %s", e.getMessage()));
        }

        Set<DetectorType> detectorTypes = new HashSet<>();
        if (runResultOptional.isPresent()) {
            detectorTypes.addAll(runResultOptional.get().getApplicableDetectors());
        }

        String requiredDetectors = detectConfiguration.getProperty(DetectProperty.DETECT_REQUIRED_DETECTOR_TYPES, PropertyAuthority.None);
        RequiredDetectorChecker requiredDetectorChecker = new RequiredDetectorChecker();
        requiredDetectorChecker.checkForMissingDetectors(requiredDetectors, detectorTypes);

        boolean printOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT, PropertyAuthority.None);

        ExitCodeType detectExitCode = exitCodeManager.getWinningExitCode();
        if (!printOutput) {
            reportManager.printDetectorIssues();
            detectStatusManager.logDetectResults(new Slf4jIntLogger(logger), detectExitCode);
        }

        return detectExitCode;
    }
}
