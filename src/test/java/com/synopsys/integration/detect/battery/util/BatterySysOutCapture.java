/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.battery.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatterySysOutCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    ByteArrayOutputStream out;
    PrintStream old;

    public void startCapture() {
        try {
            old = System.out;
            out = new ByteArrayOutputStream();
            final PrintStream stream = new PrintStream(out);

            final TeeOutputStream myOut = new TeeOutputStream(System.out, stream);
            final PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);

        } catch (final Exception e) {
            logger.info("Failed to capture sysout.", e);
        }
    }

    public List<String> stopCapture() {
        System.setOut(old);
        return Arrays.asList(out.toString().split(System.lineSeparator()));
    }
}
