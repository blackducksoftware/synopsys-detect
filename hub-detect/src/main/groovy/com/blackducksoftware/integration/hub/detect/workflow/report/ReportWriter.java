package com.blackducksoftware.integration.hub.detect.workflow.report;

public interface ReportWriter {
    public void writeLine();

    public void writeLine(final String line);

    public void writeSeperator();

    public void writeHeader();

    public void finish();
}
