package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class DiagnosticReportWriter {

    private BufferedWriter writer;

    public DiagnosticReportWriter(final File reportFile, final String name, final String description, final String runId) throws DetectUserFriendlyException {
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
            throw new DetectUserFriendlyException("Diagnostics mode failed to create a diagnostic report.", ExitCodeType.FAILURE_DIAGNOSTIC);
        }
    }

    public void writeLine(final String line) {
        try {
            writer.append(line);
            writer.newLine();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSeperator() {
        writeLine(ReportConstants.SEPERATOR);
    }

    public void finish() {
        try {
            writer.flush();
            writer.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
