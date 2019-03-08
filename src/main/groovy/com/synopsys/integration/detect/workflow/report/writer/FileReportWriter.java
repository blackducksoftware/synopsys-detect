/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.report.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;

public class FileReportWriter implements ReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BufferedWriter writer;
    private FileWriter fileWriter;

    public FileReportWriter(final File reportFile, final String name, final String description, final String runId) throws DetectUserFriendlyException {
        try {
            fileWriter = new FileWriter(reportFile, true);
            writer = new BufferedWriter(fileWriter);
            writeSeperator();
            writer.newLine();
            writer.append("Report: " + name);
            writer.newLine();
            writer.append("Run id: " + runId);
            writer.newLine();
            writer.append(description);
            writer.newLine();
            writer.newLine();
            writeSeperator();
        } catch (final Exception e) {
            logger.error("Diagnostics failed to create a report.", e);
        }
    }

    @Override
    public void writeLine(final String line) {
        try {
            writer.append(line);
            writer.newLine();
        } catch (final Exception e) {
            logger.error("Failed to write line.", e);
        }
    }

    @Override
    public void writeLine(final String line, final Exception e) {
        //TODO: print full exception if applicable.
        writeLine(line);
        writeLine(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()){
            writeLine(element.toString());
        }
    }

    @Override
    public void writeLine() {
        writeLine("");
    }

    @Override
    public void writeSeperator() {
        writeLine(ReportConstants.SEPERATOR);
    }

    @Override
    public void writeHeader() {
        writeLine(ReportConstants.HEADING);
    }

    @Override
    public void finish() {
        try {
            writer.flush();
        } catch (final Exception e) {
            logger.error("Failed to flush report.", e);
        }
        try {
            writer.close();
        } catch (final Exception e) {
            logger.error("Failed to close report.", e);
        }
        try {
            fileWriter.close();
        } catch (final Exception e) {
            logger.error("Failed to close report writer.", e);
        }
    }
}
