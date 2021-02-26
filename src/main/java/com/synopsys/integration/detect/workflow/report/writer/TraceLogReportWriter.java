/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceLogReportWriter extends LogReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void writeLine(final String line) {
        logger.trace(line);
    }

    @Override
    public void writeLine(final String line, final Exception e) {
        logger.trace(line, e);
    }
}
