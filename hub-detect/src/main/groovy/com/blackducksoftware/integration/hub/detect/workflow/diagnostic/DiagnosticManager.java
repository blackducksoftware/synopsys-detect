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

    private String runId;
    private File outputDirectory;
    private File reportDirectory;
    private File cleanupDirectory;
    private File extractionDirectory;

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
        System.out.println("Diagnostic mode on. Run id " + runId);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("");

        outputDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH));
        reportDirectory = new File(new File(outputDirectory, "reports"), runId);
        cleanupDirectory = new File(new File(outputDirectory, "cleanup"), runId);
        extractionDirectory = new File(new File(outputDirectory, "extractions"), runId);
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

            reportDirectory.delete();
            extractionDirectory.delete();
            cleanupDirectory.delete();
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
        addIfExists(reportDirectory, directoriesToCompress);
        addIfExists(cleanupDirectory, directoriesToCompress);
        addIfExists(extractionDirectory, directoriesToCompress);

        final DiagnosticZipCreator zipper = new DiagnosticZipCreator();
        zipper.createDiagnosticZip(runId, outputDirectory, directoriesToCompress);
    }

    public void trackFile(final File file) {
        try {
            if (file.isFile()) {
                final File dest = findNextAvailable(file.getName());
                FileUtils.moveFile(file, dest);

            } else if (file.isDirectory()) {
                final File dest = findNextAvailable(file.getName());
                FileUtils.moveDirectory(file, dest);
            }
        } catch (final Exception e) {

        }
    }

    private File findNextAvailable(final String name) {
        final File given = new File(name);
        if (given.exists()) {
            return findNextAvailable(name, 1);
        } else {
            return given;
        }

    }

    private File findNextAvailable(final String name, final int attempt) {
        final File next = new File(cleanupDirectory, name + "_" + attempt);
        if (next.exists()) {
            return findNextAvailable(name, attempt + 1);
        } else {
            return next;
        }
    }

    public boolean isDiagnosticModeOn() {
        return true;
    }

}
