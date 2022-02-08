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
            PrintStream stream = new PrintStream(out);

            TeeOutputStream myOut = new TeeOutputStream(System.out, stream);
            PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);

        } catch (Exception e) {
            logger.info("Failed to capture sysout.", e);
        }
    }

    public List<String> stopCapture() {
        System.setOut(old);
        return Arrays.asList(out.toString().split(System.lineSeparator()));
    }
}
