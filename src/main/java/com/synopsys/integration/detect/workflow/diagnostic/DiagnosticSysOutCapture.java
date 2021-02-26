/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Logging TRACE to console is a bad idea. Here we will log DEBUG to console, and more verbose levels to files.
public class DiagnosticSysOutCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final File stdOutFile;
    private FileOutputStream stdOutStream;

    public DiagnosticSysOutCapture(final File stdOutFile) {
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
