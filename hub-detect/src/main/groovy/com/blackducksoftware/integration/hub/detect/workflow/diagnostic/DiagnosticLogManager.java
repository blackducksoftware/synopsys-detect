package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class DiagnosticLogManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String logFilePath = "log.txt";
    private static String stdOutFilePath = "out.txt";

    private File reportDirectory;
    private String runId;
    private File stdOutFile;
    private FileOutputStream stdOutStream;

    public void init(final File reportDirectory, final String runId) {
        this.reportDirectory = reportDirectory;
        this.runId = runId;

        setLevel(Level.ALL);
        try {
            addAppender(getLogFile().getCanonicalPath());
        } catch (final IOException e1) {
            e1.printStackTrace();
        }

        captureStdOut();
    }

    public void finish() {
        closeOut();
    }

    public void cleanup() {
        getStdOutFile().delete();
        getLogFile().delete();
    }

    private void captureStdOut() {
        try {
            stdOutFile = new File(reportDirectory, stdOutFilePath);
            stdOutStream = new FileOutputStream(stdOutFile);
            final TeeOutputStream myOut = new TeeOutputStream(System.out, stdOutStream);
            final PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);
            System.out.println("Diagnostic mode on. Run id " + runId);
            System.out.println("Writing to log file: " + stdOutFile.getCanonicalPath());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void setLevel(final Level targetLevel) {
        logger.info("Attempting to set log level.");
        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.trace("Is it success?");
        root.setLevel(Level.ALL);
        logger.trace("how bout nough?");
    }

    private void addAppender(final String file) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        final FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();

        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        logbackLogger.addAppender(fileAppender);
        logbackLogger.setLevel(Level.ALL);
    }

    private File getStdOutFile() {
        final File dest = new File(reportDirectory, stdOutFilePath);
        return dest;
    }

    private File getLogFile() {
        final File dest = new File(reportDirectory, logFilePath);
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
