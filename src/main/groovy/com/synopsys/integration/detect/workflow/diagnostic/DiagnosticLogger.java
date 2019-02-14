/**
 * detect-application
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
package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class DiagnosticLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String logFilePath = "log.txt";
    private static String stdOutFilePath = "out.txt";
    private static final String LOGBACK_LOGGER_NAME = "com.blackducksoftware.integration";

    private File logDirectory;
    private File stdOutFile;
    private FileOutputStream stdOutStream;
    private FileAppender<ILoggingEvent> fileAppender;
    private FileAppender<ILoggingEvent> extractionAppender;

    public DiagnosticLogger(File logDirectory, EventSystem eventSystem) {

        this.logDirectory = logDirectory;

        logger.info("Attempting to set log level.");
        setLevel(Level.ALL);

        logger.info("Attempting to redirect log messages.");
        try {
            fileAppender = addAppender(getLogFile().getCanonicalPath());
        } catch (final IOException e) {
            logger.info("Failed to redirect.", e);
        }

        logger.info("Attempting to redirect sysout.");
        captureStdOut();

        eventSystem.registerListener(Event.ExtractionStarted, it -> startLoggingExtraction(it.getExtractionId()));
        eventSystem.registerListener(Event.ExtractionEnded, it -> stopLoggingExtraction(it.getExtractionId()));
    }

    public void finish() {
        closeOut();
        fileAppender.stop();
    }

    private void captureStdOut() {
        try {
            stdOutFile = new File(logDirectory, stdOutFilePath);
            stdOutStream = new FileOutputStream(stdOutFile);
            final TeeOutputStream myOut = new TeeOutputStream(System.out, stdOutStream);
            final PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);

            logger.info("Writing sysout to file: " + stdOutFile.getCanonicalPath());

        } catch (final Exception e) {
            logger.info("Failed to capture sysout.", e);
        }
    }

    public void startLoggingExtraction(final ExtractionId extractionId) {
        logger.info("Diagnostics attempting to redirect extraction logs: " + extractionId.toUniqueString());
        final File logDir = new File(logDirectory, "extractions");
        logDir.mkdirs();
        final File logFile = new File(logDir, extractionId.toUniqueString() + ".txt");
        try {
            final String logFilePath = logFile.getCanonicalPath();
            extractionAppender = addAppender(logFilePath);
            logger.info("Redirected to file: " + logFilePath);
        } catch (final IOException e) {
            logger.info("Failed to redirect.", e);
        }

    }

    public void stopLoggingExtraction(final ExtractionId extractionId) {
        logger.info("Diagnostics finished redirecting for extraction: " + extractionId.toUniqueString());
        if (extractionAppender != null) {
            removeAppender(extractionAppender);
            extractionAppender.stop();
        }
    }

    private void setLevel(final Level targetLevel) {
        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LOGBACK_LOGGER_NAME);
        root.setLevel(Level.ALL);
    }

    private void removeAppender(final FileAppender<ILoggingEvent> appender) {
        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LOGBACK_LOGGER_NAME);
        logbackLogger.detachAppender(extractionAppender);
    }

    private FileAppender<ILoggingEvent> addAppender(final String file) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> appender;
        appender = new FileAppender<>();
        appender.setFile(file);
        appender.setEncoder(ple);
        appender.setContext(lc);
        appender.start();

        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LOGBACK_LOGGER_NAME);
        logbackLogger.addAppender(appender);
        logbackLogger.setLevel(Level.ALL);

        return appender;
    }

    private File getStdOutFile() {
        final File dest = new File(logDirectory, stdOutFilePath);
        return dest;
    }

    private File getLogFile() {
        final File dest = new File(logDirectory, logFilePath);
        return dest;
    }

    private void closeOut() {
        try {
            stdOutStream.flush();
            stdOutStream.close();
            stdOutFile.renameTo(getStdOutFile());

        } catch (final Exception e) {
            logger.debug("Failed to close out", e);
        }
    }

}
