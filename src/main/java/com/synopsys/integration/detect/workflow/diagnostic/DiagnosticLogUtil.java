package com.synopsys.integration.detect.workflow.diagnostic;

import org.slf4j.LoggerFactory;

public class DiagnosticLogUtil {
    public static ch.qos.logback.classic.Logger getLogger(String named) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(named);
        return logger;
    }

    public static ch.qos.logback.classic.Logger getOurLogger() {
        return getLogger("com.synopsys.integration");
    }

    public static ch.qos.logback.classic.Logger getRootLogger() {
        return getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }
}
