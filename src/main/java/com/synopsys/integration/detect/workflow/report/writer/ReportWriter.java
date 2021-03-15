/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.writer;

public interface ReportWriter {
    void writeLine();

    void writeLine(final String line);

    void writeLine(final String line, Exception e);

    void writeSeparator();

    void writeHeader();

    void finish();
}
