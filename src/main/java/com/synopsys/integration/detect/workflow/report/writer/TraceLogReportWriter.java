package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceLogReportWriter extends LogReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void writeLine(String line) {
        logger.trace(line);
    }

    @Override
    public void writeLine(String line, Exception e) {
        logger.trace(line, e);
    }
}
