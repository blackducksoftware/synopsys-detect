package com.synopsys.integration.detect.workflow.diagnostic;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class DiagnosticLogUtil {
    public static ch.qos.logback.classic.Logger getLogger(String named) {
        final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(named);
        return logger;
    }

    public static ch.qos.logback.classic.Logger getOurLogger() {
        return getLogger("com.synopsys.integration");
    }

    public static ch.qos.logback.classic.Logger getRootLogger() {
        return getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    }
}
