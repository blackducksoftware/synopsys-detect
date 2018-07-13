package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private File cleanupDirectory;
    private File extractionDirectory;
    private File bdioDirectory;

    private final DiagnosticReportManager diagnosticReportManager;
    private final DiagnosticLogManager diagnosticLogManager;
    private final DetectRunManager detectRunManager;

    public DiagnosticManager(final DetectConfigWrapper detectConfigWrapper, final BomToolProfiler profiler, final DiagnosticReportManager diagnosticReportManager, final DiagnosticLogManager diagnosticLogManager,
            final DetectRunManager detectRunManager) {
        this.detectConfigWrapper = detectConfigWrapper;
        this.profiler = profiler;
        this.diagnosticReportManager = diagnosticReportManager;
        this.diagnosticLogManager = diagnosticLogManager;
        this.detectRunManager = detectRunManager;
    }

    public void init() {

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

        diagnosticReportManager.init(reportDirectory, detectRunManager.getRunId());
        diagnosticLogManager.init(reportDirectory, detectRunManager.getRunId());
    }

    public void finish() {
        writeReports();

        diagnosticReportManager.finish();
        diagnosticLogManager.finish();

        createZip();

        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_CLEANUP)) {
            diagnosticReportManager.cleanup();
            diagnosticLogManager.cleanup();

            bdioDirectory.delete();
            reportDirectory.delete();
            extractionDirectory.delete();
            relevantDirectory.delete();
        }

    }

    public void writeReports() {
        profiler.writeToLogs();

        final ProfilingReporter reporter = new ProfilingReporter();
        final DiagnosticReportWriter applicableReport = diagnosticReportManager.getReportWriter(ReportTypes.APPLICABLE_PROFILE);
        reporter.writeReport(applicableReport, profiler);

        final DiagnosticReportWriter extractionReport = diagnosticReportManager.getReportWriter(ReportTypes.EXTRACTION);
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

    private void createZip() {
        final List<File> directoriesToCompress = new ArrayList<>();
        addIfExists(bdioDirectory, directoriesToCompress);
        addIfExists(reportDirectory, directoriesToCompress);
        addIfExists(relevantDirectory, directoriesToCompress);
        addIfExists(extractionDirectory, directoriesToCompress);

        final DiagnosticZipCreator zipper = new DiagnosticZipCreator();
        zipper.createDiagnosticZip(detectRunManager.getRunId(), outputDirectory, directoriesToCompress);
    }

    public void trackFile(final File file) {
        if (file != null || file == null) {
            return;
        }
    }

    public void registerFileOfInterest(final ExtractionId extractionId, final File file) {
        registerFileOfInterest(file, extractionId.toUniqueString());
    }

    public void registerGlobalFileOfInterest(final File file) {
        registerFileOfInterest(file, "global");
    }

    private void registerFileOfInterest(final File file, final String directoryName) {
        if (isProtectedModeOn()) {
            return; // don't track any customer files
        }
        try {
            if (file == null) {
                return;
            } else if (file.isFile()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyFile(file, dest);
            } else if (file.isDirectory()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyDirectory(file, dest);
            }
        } catch (final Exception e) {

        }
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

    public boolean isDiagnosticModeOn() {
        return true;
    }

    /*
     * If this returns true, customer files or anything related to customer source should NOT be collected during diagnostics. Otherwise, things like lock files, solutions files, build reports may be collected during diagnostics.
     */
    public boolean isProtectedModeOn() {
        return false;
    }
}
