package com.blackducksoftware.integration.hub.detect.diagnostic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;

@Component
public class DiagnosticsManager {
    private final Logger logger = LoggerFactory.getLogger(DiagnosticsManager.class);

    private String runId;

    @Autowired
    private DetectConfiguration detectConfiguration;

    private File logFile;
    //    private File runDirectory;
    private File reportDirectory;
    private FileOutputStream logOut;
    private final Map<ReportTypes, BufferedWriter> reportWriters = new HashMap<>();

    enum ReportTypes{
        search,
        extraction,
        preparation
    }

    public void init() {

        //if (diagnosticMode)
        reportDirectory = new File(detectConfiguration.getOutputDirectory(), "reports");
        reportDirectory.mkdir();

        runId = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));

        try {
            logFile = new File(reportDirectory, "log.txt");
            logOut = new FileOutputStream(logFile);
            final TeeOutputStream myOut=new TeeOutputStream(System.out, logOut);
            final PrintStream ps = new PrintStream(myOut, true); //true - auto-flush after println
            System.setOut(ps);
            System.out.println("Diagnostic mode on. Run id " + runId);
            System.out.println("Writing to log file: " + logFile.getCanonicalPath());

        } catch (final Exception e) {
            e.printStackTrace();
        }

        createReportWriter(ReportTypes.search);
        createReportWriter(ReportTypes.extraction);
        createReportWriter(ReportTypes.preparation);
    }

    private void createReportWriter(final ReportTypes type) {
        final File searchReport = new File(reportDirectory, type.toString() + ".txt");
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(searchReport, true));
            reportWriters.put(type, writer);
            writer.append(runId);
            writer.newLine();
            writer.newLine();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void printToReport(final ReportTypes type, final String line) {
        if (reportWriters.containsKey(type)) {
            try {
                final BufferedWriter writer = reportWriters.get(type);
                writer.append(line);
                writer.newLine();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void printToSearchReport(final String line) {
        printToReport(ReportTypes.search, line);
    }

    public void printToExtractionReport(final String line) {
        printToReport(ReportTypes.extraction, line);
    }

    public void printToPreparationReport(final String line) {
        printToReport(ReportTypes.preparation, line);
    }

    public void writeNeedReport() {

    }

    public void writeDemandReport() {

    }

    public void writeExtractionReport() {

    }

    public void writeCodeLocationReport() {

    }

    public void createDiagnosticZip() {
        for (final BufferedWriter writer : reportWriters.values()) {
            try {
                writer.flush();
                writer.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        try {
            logOut.flush();
            logOut.close();

            final File dest = new File(reportDirectory, "log.txt");

            logFile.renameTo(dest);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        logger.info("Run id: " + runId);
        try {
            final File zip = new File(detectConfiguration.getOutputDirectory(), "detect-run-" + runId + ".zip");
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
            final File extractions = new File(detectConfiguration.getOutputDirectory(), "extractions");
            ZipCompress.compress(outputStream, detectConfiguration.getOutputDirectory().toPath(), extractions.toPath(), zip);
            ZipCompress.compress(outputStream, detectConfiguration.getOutputDirectory().toPath(), reportDirectory.toPath(), zip);
            logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
            outputStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (detectConfiguration.getCleanupDetectFiles()) {
            for (final File file : reportDirectory.listFiles()) {
                try {
                    file.delete();
                } catch (final SecurityException e) {
                    logger.error("Failed to cleanup: " + file.getPath());
                    e.printStackTrace();
                }
            }
        }
    }

}
