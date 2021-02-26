/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
    public void writeLine(final String line) {
        logger.error(line);
    }

    @Override
    public void writeLine(final String line, final Exception e) {
        logger.error(line, e);
    }
}
