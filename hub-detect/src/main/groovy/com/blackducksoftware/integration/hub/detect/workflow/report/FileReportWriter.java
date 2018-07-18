package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class DiagnosticFileReportWriter implements DiagnosticReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BufferedWriter writer;

    public DiagnosticFileReportWriter(final File reportFile, final String name, final String description, final String runId) throws DetectUserFriendlyException {
        try {
            final FileWriter fileWriter = new FileWriter(reportFile, true);
            writer = new BufferedWriter(fileWriter);
            writeSeperator();
            writer.newLine();
            writer.append("Report: " + name);
            writer.newLine();
            writer.append("Run id: " + runId);
            writer.newLine();
            writer.append(description);
            writer.newLine();
            writer.newLine();
            writeSeperator();
        } catch (final Exception e) {
            logger.error("Diagnostics failed to create a report.");
            e.printStackTrace();
        }
    }

    @Override
    public void writeLine(final String line) {
        try {
            writer.append(line);
            writer.newLine();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeSeperator() {
        writeLine(ReportConstants.SEPERATOR);
    }

    @Override
    public void finish() {
        try {
            writer.flush();
            writer.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
