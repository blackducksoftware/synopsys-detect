package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugLogReportWriter extends LogReportWriter {
    private final Logger logger;

    public DebugLogReportWriter() {
        this(LoggerFactory.getLogger(InfoLogReportWriter.class));
    }

    public DebugLogReportWriter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeLine(String line) {
        logger.debug(line);
    }

    @Override
    public void writeLine(String line, Exception e) {
        logger.debug(line, e);
    }
}

