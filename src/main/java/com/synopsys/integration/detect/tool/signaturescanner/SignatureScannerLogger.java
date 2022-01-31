package com.synopsys.integration.detect.tool.signaturescanner;

import org.slf4j.Logger;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;

public class SignatureScannerLogger extends IntLogger {

    private final Logger logger;

    public SignatureScannerLogger(Logger logger) {
        this.logger = logger;
    }

    private boolean shouldInfo(String line) {
        //redirect sig scan INFO level to DEBUG.
        return !line.contains("INFO: ");
    }

    @Override
    public void alwaysLog(String txt) {
        logger.info(txt);
    }

    @Override
    public void info(String txt) {
        if (shouldInfo(txt)) {
            logger.info(txt);
        } else {
            debug(txt);
        }
    }

    @Override
    public void error(Throwable t) {
        logger.error("", t);
    }

    @Override
    public void error(String txt, Throwable t) {
        logger.error(txt, t);

    }

    @Override
    public void error(String txt) {
        logger.error(txt);

    }

    @Override
    public void warn(String txt) {
        logger.warn(txt);

    }

    @Override
    public void trace(String txt) {
        logger.trace(txt);

    }

    @Override
    public void trace(String txt, Throwable t) {
        logger.trace(txt, t);

    }

    @Override
    public void debug(String txt) {
        logger.debug(txt);

    }

    @Override
    public void debug(String txt, Throwable t) {
        logger.debug(txt, t);
    }

    @Override
    public void setLogLevel(LogLevel logLevel) {
        // TODO: Actually do something?
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }
}
