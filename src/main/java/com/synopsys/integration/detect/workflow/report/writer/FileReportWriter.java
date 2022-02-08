package com.synopsys.integration.detect.workflow.report.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.report.util.ReportConstants;

public class FileReportWriter implements ReportWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BufferedWriter writer;
    private FileWriter fileWriter;

    public FileReportWriter(File reportFile, String name, String description, String runId) {
        try {
            fileWriter = new FileWriter(reportFile, true);
            writer = new BufferedWriter(fileWriter);
            writeSeparator();
            writer.newLine();
            writer.append("Report: " + name);
            writer.newLine();
            writer.append("Run id: " + runId);
            writer.newLine();
            writer.append(description);
            writer.newLine();
            writer.newLine();
            writeSeparator();
        } catch (Exception e) {
            logger.error("Diagnostics failed to create a report.", e);
        }
    }

    @Override
    public void writeLine(String line) {
        try {
            writer.append(line);
            writer.newLine();
        } catch (Exception e) {
            logger.error("Failed to write line.", e);
        }
    }

    @Override
    public void writeLine(String line, Exception e) {
        writeLine(line);
        writeLine(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            writeLine(element.toString());
        }
    }

    @Override
    public void writeLine() {
        writeLine("");
    }

    @Override
    public void writeSeparator() {
        writeLine(ReportConstants.SEPARATOR);
    }

    @Override
    public void writeHeader() {
        writeLine(ReportConstants.HEADING);
    }

    @Override
    public void finish() {
        try {
            writer.flush();
        } catch (Exception e) {
            logger.error("Failed to flush report.", e);
        }
        try {
            writer.close();
        } catch (Exception e) {
            logger.error("Failed to close report.", e);
        }
        try {
            fileWriter.close();
        } catch (Exception e) {
            logger.error("Failed to close report writer.", e);
        }
    }
}
