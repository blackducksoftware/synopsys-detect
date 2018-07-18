package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

public interface DiagnosticReportWriter {
    public void writeLine(final String line);

    public void writeSeperator();

    public void finish();
}
