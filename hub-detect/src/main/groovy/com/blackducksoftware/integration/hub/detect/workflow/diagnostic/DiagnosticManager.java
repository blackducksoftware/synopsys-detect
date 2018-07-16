package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.testutils.ObjectPrinter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticReportManager.ReportTypes;

public class DiagnosticManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectConfigWrapper detectConfigWrapper;
    private final BomToolProfiler profiler;

    private File outputDirectory;
    private File reportDirectory;
    private File relevantDirectory;
    private File extractionDirectory;
    private File bdioDirectory;

    private final DiagnosticReportManager diagnosticReportManager;
    private final DiagnosticLogManager diagnosticLogManager;
    private final DetectRunManager detectRunManager;

    private boolean isDiagnosticProtected = false;
    private boolean isDiagnostic = false;

    public DiagnosticManager(final DetectConfigWrapper detectConfigWrapper, final BomToolProfiler profiler, final DiagnosticReportManager diagnosticReportManager, final DiagnosticLogManager diagnosticLogManager,
            final DetectRunManager detectRunManager) {
        this.detectConfigWrapper = detectConfigWrapper;
        this.profiler = profiler;
        this.diagnosticReportManager = diagnosticReportManager;
        this.diagnosticLogManager = diagnosticLogManager;
        this.detectRunManager = detectRunManager;
    }

    public void init(final boolean isDiagnostic, final boolean isDiagnosticProtected) {

        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticProtected = isDiagnosticProtected;

        if (!isDiagnostic) {
            return;
        }

        System.out.println("");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Diagnostic mode on. Run id " + detectRunManager.getRunId());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("");

        outputDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH));
        bdioDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH));
        reportDirectory = new File(new File(outputDirectory, "reports"), detectRunManager.getRunId());
        relevantDirectory = new File(new File(outputDirectory, "relevant"), detectRunManager.getRunId());
        extractionDirectory = new File(new File(outputDirectory, "extractions"), detectRunManager.getRunId());
        reportDirectory.mkdir();
        relevantDirectory.mkdir();
        extractionDirectory.mkdir();

        logger.info("Diagnostics bdio directory: " + bdioDirectory.getPath());
        logger.info("Diagnostics report directory: " + reportDirectory.getPath());
        logger.info("Diagnostics extraction directory: " + extractionDirectory.getPath());
        logger.info("Diagnostics relevant directory: " + relevantDirectory.getPath());

        diagnosticReportManager.init(reportDirectory, detectRunManager.getRunId());
        diagnosticLogManager.init(reportDirectory);

        logger.info("Diagnostic mode on. Run id " + detectRunManager.getRunId());
    }

    public void finish() {
        if (!isDiagnosticModeOn()) {
            return;
        }

        writeReports();

        diagnosticReportManager.finish();
        diagnosticLogManager.finish();

        logger.info("Preparing to create diagnostics zip.");
        final boolean zipCreated = createZip();

        if (zipCreated) {
            if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_CLEANUP)) {
                try {
                    logger.info("Cleaning bdio directory: " + bdioDirectory.getPath());
                    FileUtils.deleteDirectory(bdioDirectory);
                    logger.info("Cleaning report directory: " + reportDirectory.getPath());
                    FileUtils.deleteDirectory(reportDirectory);
                    logger.info("Cleaning extraction directory: " + extractionDirectory.getPath());
                    FileUtils.deleteDirectory(extractionDirectory);
                    logger.info("Cleaning relevant directory: " + relevantDirectory.getPath());
                    FileUtils.deleteDirectory(relevantDirectory);
                } catch (final IOException e) {
                    logger.error("Failed to cleanup:");
                    e.printStackTrace();
                }
            }
        } else {
            logger.warn("Failed to create diagnostics zip. Cleanup will not occur.");
        }

    }

    public boolean isDiagnosticModeOn() {
        return isDiagnostic;
    }

    /*
     * If this returns true, customer files or anything related to customer source should NOT be collected during diagnostics. Otherwise, things like lock files, solutions files, build reports may be collected during diagnostics.
     */
    public boolean isProtectedModeOn() {
        return isDiagnosticProtected;
    }

    public void registerFileOfInterest(final ExtractionId extractionId, final File file) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        registerFileOfInterest(file, extractionId.toUniqueString());
    }

    public void registerGlobalFileOfInterest(final File file) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        registerFileOfInterest(file, "global");
    }

    public void startLoggingExtraction(final ExtractionId extractionId) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticLogManager.startLoggingExtraction(extractionId);
    }

    public void stopLoggingExtraction(final ExtractionId extractionId) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticLogManager.stopLoggingExtraction(extractionId);
    }

    private void writeReports() {
        final DiagnosticReportWriter profileWriter = diagnosticReportManager.getReportWriter(ReportTypes.BOM_TOOL_PROFILE);

        final ProfilingReporter reporter = new ProfilingReporter();
        profileWriter.writeSeperator();
        profileWriter.writeLine("Applicable Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, profiler.getApplicableTimings());
        profileWriter.writeSeperator();
        profileWriter.writeLine("Extractable Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, profiler.getExtractableTimings());
        profileWriter.writeSeperator();
        profileWriter.writeLine("Extraction Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, profiler.getExtractionTimings());

        final DiagnosticReportWriter extractionReport = diagnosticReportManager.getReportWriter(ReportTypes.EXTRACTION_STATE);
        for (final BomToolTime time : profiler.getExtractionTimings()) {
            extractionReport.writeSeperator();
            extractionReport.writeLine(time.getBomTool().getDescriptiveName());
            ObjectPrinter.printObjectPrivate(extractionReport, null, time.getBomTool());
            extractionReport.writeSeperator();
        }
    }

    private void addIfExists(final File file, final List<File> files) {
        if (file.exists()) {
            files.add(file);
        }
    }

    private boolean createZip() {
        final List<File> directoriesToCompress = new ArrayList<>();
        addIfExists(bdioDirectory, directoriesToCompress);
        addIfExists(reportDirectory, directoriesToCompress);
        addIfExists(relevantDirectory, directoriesToCompress);
        addIfExists(extractionDirectory, directoriesToCompress);

        final DiagnosticZipCreator zipper = new DiagnosticZipCreator();
        return zipper.createDiagnosticZip(detectRunManager.getRunId(), outputDirectory, directoriesToCompress);
    }

    private void registerFileOfInterest(final File file, final String directoryName) {
        if (isProtectedModeOn()) {
            return; // don't track any customer files
        }
        try {
            if (file == null) {
                return;
            }
            if (isChildOfTrackedFolder(file)) {
                logger.info("Asked to track file '" + file.getPath() + "' but it is already being tracked.");
                return;
            }
            if (file.isFile()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyFile(file, dest);
            } else if (file.isDirectory()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyDirectory(file, dest);
            }
        } catch (final Exception e) {
            logger.trace("Failed to copy file to relevant directory:" + file.toString());
        }
    }

    private boolean isChildOfTrackedFolder(final File file) {
        if (file.toPath().startsWith(bdioDirectory.toPath())) {
            return true;
        } else if (file.toPath().startsWith(reportDirectory.toPath())) {
            return true;
        } else if (file.toPath().startsWith(relevantDirectory.toPath())) {
            return true;
        } else if (file.toPath().startsWith(extractionDirectory.toPath())) {
            return true;
        }
        return false;
    }

    private File findNextAvailableRelevant(final String directoryName, final String name) {
        final File given = new File(new File(relevantDirectory, directoryName), name);
        if (given.exists()) {
            return findNextAvailableRelevant(directoryName, name, 1);
        } else {
            return given;
        }
    }

    private File findNextAvailableRelevant(final String directoryName, final String name, final int attempt) {
        final File next = new File(new File(relevantDirectory, directoryName), name + "_" + attempt);
        if (next.exists()) {
            return findNextAvailableRelevant(directoryName, name, attempt + 1);
        } else {
            return next;
        }
    }

}
