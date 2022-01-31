package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoLogReportWriter extends LogReportWriter {
    private final Logger logger;

    public InfoLogReportWriter() {
        this(LoggerFactory.getLogger(InfoLogReportWriter.class));
    }

    public InfoLogReportWriter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeLine(String line) {
        logger.info(line);
    }

    @Override
    public void writeLine(String line, Exception e) {
        logger.info(line, e);
    }
}
