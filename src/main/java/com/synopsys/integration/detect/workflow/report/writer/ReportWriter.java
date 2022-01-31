package com.synopsys.integration.detect.workflow.report.writer;

public interface ReportWriter {
    void writeLine();

    void writeLine(String line);

    void writeLine(String line, Exception e);

    void writeSeparator();

    void writeHeader();

    void finish();
}
