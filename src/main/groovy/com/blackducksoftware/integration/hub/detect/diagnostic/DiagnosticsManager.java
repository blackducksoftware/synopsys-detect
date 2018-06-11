/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

@Component
public class DiagnosticsManager {
    private final Logger logger = LoggerFactory.getLogger(DiagnosticsManager.class);

    private String runId;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private Profiler profiler;

    private File stdOutFile;
    private FileOutputStream stdOutStream;

    //    private File runDirectory;
    private File reportDirectory;
    private final Map<ReportTypes, DiagnosticReportWriter> reportWriters = new HashMap<>();

    private static String logFilePath = "log.txt";
    private static String stdOutFilePath = "out.txt";

    enum ReportTypes{
        search,
        extraction,
        preparation,
        extractionProfile,
        codeLocations
    }

    public void init() {

        createRunId();
        setLevel(Level.ALL);

        //if (diagnosticMode)
        reportDirectory = new File(detectConfiguration.getOutputDirectory(), "reports-" + runId);
        reportDirectory.mkdir();

        try {
            addAppender(new File(reportDirectory, logFilePath).getCanonicalPath());
        } catch (final IOException e1) {
            e1.printStackTrace();
        }

        captureStdOut();
        createReports();
    }

    private void createReports() {
        for (final ReportTypes reportType : ReportTypes.values()) {
            createReportWriter(reportType);
        }
    }

    private void createRunId() {
        runId = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
    }

    private void captureStdOut() {
        try {
            stdOutFile = new File(reportDirectory, stdOutFilePath);
            stdOutStream = new FileOutputStream(stdOutFile);
            final TeeOutputStream myOut=new TeeOutputStream(System.out, stdOutStream);
            final PrintStream ps = new PrintStream(myOut, true); //true - auto-flush after println
            System.setOut(ps);
            System.out.println("Diagnostic mode on. Run id " + runId);
            System.out.println("Writing to log file: " + stdOutFile.getCanonicalPath());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void setLevel(final Level targetLevel) {
        logger.info("Attempting to set log level.");
        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.trace("Is it success?");
        root.setLevel(Level.ALL);
        logger.trace("how bout nough?");
    }

    private void addAppender(final String file) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        final FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();

        final ch.qos.logback.classic.Logger logbackLogger =
                (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        logbackLogger.addAppender(fileAppender);
        logbackLogger.setLevel(Level.ALL);
    }

    private DiagnosticReportWriter createReportWriter(final ReportTypes type) {
        try {
            final File reportFile = new File(reportDirectory, type.toString() + ".txt");
            final DiagnosticReportWriter diagnosticReportWriter = new DiagnosticReportWriter(reportFile, type.toString(), runId);
            reportWriters.put(type, diagnosticReportWriter);
            return diagnosticReportWriter;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DiagnosticReportWriter getReportWriter(final ReportTypes type) {
        if (reportWriters.containsKey(type)) {
            return reportWriters.get(type);
        } else {
            return createReportWriter(type);
        }
    }

    private void closeReportWriters() {
        for (final DiagnosticReportWriter writer : reportWriters.values()) {
            writer.finish();
        }
    }

    private void closeOut() {
        try {
            stdOutStream.flush();
            stdOutStream.close();

            final File dest = new File(reportDirectory, stdOutFilePath);

            stdOutFile.renameTo(dest);

        } catch (final Exception e) {
            logger.debug("Failed to close out", e);
        }
    }

    private void cleanupReportFiles() {
        if (detectConfiguration.getCleanupDetectFiles()) {
            for (final File file : reportDirectory.listFiles()) {
                try {
                    file.delete();
                } catch (final SecurityException e) {
                    logger.error("Failed to cleanup: " + file.getPath());
                    e.printStackTrace();
                }
            }
        }
    }

    public void finish() {
        profiler.reportToLog();

        closeReportWriters();
        closeOut();
        createDiagnosticZip();
        cleanupReportFiles();
    }

    private void createDiagnosticZip() {
        logger.info("Run id: " + runId);
        try {
            final File zip = new File(detectConfiguration.getOutputDirectory(), "detect-run-" + runId + ".zip");
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
            final File extractions = new File(detectConfiguration.getOutputDirectory(), "extractions");
            ZipCompress.compress(outputStream, detectConfiguration.getOutputDirectory().toPath(), extractions.toPath(), zip);
            ZipCompress.compress(outputStream, detectConfiguration.getOutputDirectory().toPath(), reportDirectory.toPath(), zip);
            logger.info("Diagnostics file created at: " + zip.getCanonicalPath());
            outputStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
