package com.synopsys.integration.detect.tool.signaturescanner;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.Slf4jIntLogger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SignatureScannerLogger extends IntLogger {

    private final Logger logger;

    public SignatureScannerLogger(Logger logger) {
        this.logger = logger;
    }

    private boolean shouldInfo(String line) {
        if (line.contains("INFO: ")) { //redirect sig scan INFO level to DEBUG.
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void alwaysLog(final String txt) {
        logger.info(txt);
    }

    @Override
    public void info(final String txt) {
        if (shouldInfo(txt)) {
            logger.info(txt);
        } else {
            debug(txt);
        }
    }

    @Override
    public void error(final Throwable t) {
        logger.error("", t);
    }

    @Override
    public void error(final String txt, final Throwable t) {
        logger.error(txt, t);

    }

    @Override
    public void error(final String txt) {
        logger.error(txt);

    }

    @Override
    public void warn(final String txt) {
        logger.warn(txt);

    }

    @Override
    public void trace(final String txt) {
        logger.trace(txt);

    }

    @Override
    public void trace(final String txt, final Throwable t) {
        logger.trace(txt, t);

    }

    @Override
    public void debug(final String txt) {
        logger.debug(txt);

    }

    @Override
    public void debug(final String txt, final Throwable t) {
        logger.debug(txt, t);
    }

    @Override
    public void setLogLevel(final LogLevel logLevel) {

    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }
}
