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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

//Logging TRACE to console is a bad idea. Here we will log DEBUG to console, and more verbose levels to files.
public class DiagnosticSysOutCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private File stdOutFile;
    private FileOutputStream stdOutStream;

    public DiagnosticSysOutCapture(File stdOutFile) {
        this.stdOutFile = stdOutFile;
    }

    public void startCapture() {
        captureStdOut();
    }

    public void stopCapture() {
        closeOut();
    }

    private void captureStdOut() {
        try {
            stdOutStream = new FileOutputStream(stdOutFile);
            final TeeOutputStream myOut = new TeeOutputStream(System.out, stdOutStream);
            final PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);

            logger.info("Writing sysout to file: " + stdOutFile.getCanonicalPath());

        } catch (final Exception e) {
            logger.info("Failed to capture sysout.", e);
        }
    }

    private void closeOut() {
        try {
            stdOutStream.flush();
            stdOutStream.close();

        } catch (final Exception e) {
            logger.debug("Failed to close out", e);
        }
    }
}
