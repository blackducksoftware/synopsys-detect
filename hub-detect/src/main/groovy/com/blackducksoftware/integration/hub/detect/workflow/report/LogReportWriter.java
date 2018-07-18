package com.blackducksoftware.integration.hub.detect.workflow.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class LogReportWriter implements ReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void writeLine(final String line) {
        logger.info(line);
    }

    @Override
    public void writeSeperator() {
        writeLine(ReportConstants.SEPERATOR);
    }

    @Override
    public void writeLine() {
        writeLine("");
    }

    @Override
    public void writeHeader() {
        writeLine(ReportConstants.HEADING);
    }

    @Override
    public void finish() {
    }

}
