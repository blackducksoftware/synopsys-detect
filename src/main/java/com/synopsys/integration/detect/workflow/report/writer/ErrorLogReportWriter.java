package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorLogReportWriter extends LogReportWriter {
    private final Logger logger;

    public ErrorLogReportWriter() {
        this(LoggerFactory.getLogger(ErrorLogReportWriter.class));
    }

    public ErrorLogReportWriter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeLine(String line) {
        logger.error(line);
    }

    @Override
    public void writeLine(String line, Exception e) {
        logger.error(line, e);
    }
}
