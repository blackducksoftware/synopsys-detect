/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import com.synopsys.integration.detect.tool.detector.ExtractionId;
import com.synopsys.integration.detect.tool.detector.impl.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.log.LogLevel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class DiagnosticLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String LOGBACK_LOGGER_NAME = "com.synopsys.integration";
    private FileAppender<ILoggingEvent> fileAppender;
    private File logFile;
    private Level level;


    public DiagnosticLogger(File logFile, Level level){
        this.logFile = logFile;
        this.level = level;
    }

    public void startLogging() {
        try {
            final String logFilePath = logFile.getCanonicalPath();
            fileAppender = addAppender(logFilePath);
            logger.info("Redirected to file: " + logFilePath);
        } catch (final IOException e) {
            logger.info("Failed to redirect.", e);
        }

    }

    public void stopLogging() {
        if (fileAppender != null) {
            removeAppender(fileAppender);
            fileAppender.stop();
        }
    }

    private void removeAppender(final FileAppender<ILoggingEvent> appender) {
        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LOGBACK_LOGGER_NAME);
        logbackLogger.detachAppender(appender);
    }

    private FileAppender<ILoggingEvent> addAppender(final String file) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();

        final FileAppender<ILoggingEvent> appender;
        appender = new FileAppender<>();
        appender.setFile(file);
        appender.setEncoder(ple);
        appender.setContext(lc);
        ThresholdFilter levelFilter = new ThresholdFilter();
        levelFilter.setLevel(this.level.levelStr);
        levelFilter.start();
        appender.addFilter(levelFilter);

        appender.start();

        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        logbackLogger.addAppender(appender);

        return appender;
    }
}
