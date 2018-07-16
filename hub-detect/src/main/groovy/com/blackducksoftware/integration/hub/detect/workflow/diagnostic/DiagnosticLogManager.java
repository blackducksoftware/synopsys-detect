package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;

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
    private File stdOutFile;
    private FileOutputStream stdOutStream;

    private FileAppender<ILoggingEvent> fileAppender;

    private FileAppender<ILoggingEvent> extractionAppender;

    public void init(final File reportDirectory) {
        this.reportDirectory = reportDirectory;

        logger.info("Attempting to set log level.");
        setLevel(Level.ALL);

        logger.info("Attempting to redirect log messages.");
        try {
            fileAppender = addAppender(getLogFile().getCanonicalPath());
        } catch (final IOException e1) {
            e1.printStackTrace();
        }

        logger.info("Attempting to redirect sysout.");
        captureStdOut();
    }

    public void finish() {
        closeOut();
        fileAppender.stop();
    }

    public void cleanup() {
        logger.info("Cleaning sysout file: " + getStdOutFile().getPath());
        getStdOutFile().delete();
        logger.info("Cleaning log file: " + getLogFile().getPath());
        getLogFile().delete();
    }

    private void captureStdOut() {
        try {
            stdOutFile = new File(reportDirectory, stdOutFilePath);
            stdOutStream = new FileOutputStream(stdOutFile);
            final TeeOutputStream myOut = new TeeOutputStream(System.out, stdOutStream);
            final PrintStream ps = new PrintStream(myOut, true); // true - auto-flush after println
            System.setOut(ps);

            logger.info("Writing sysout to file: " + stdOutFile.getCanonicalPath());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void setLevel(final Level targetLevel) {
        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.blackducksoftware.integration");
        root.setLevel(Level.ALL);
    }

    public void startLoggingExtraction(final ExtractionId extractionId) {
        logger.info("Diagnostics attempting to redirect extraction logs: " + extractionId.toUniqueString());
        final File logDir = new File(reportDirectory, "extraction-logs");
        logDir.mkdirs();
        final File logFile = new File(logDir, extractionId.toUniqueString() + ".txt");
        try {
            final String logFilePath = logFile.getCanonicalPath();
            extractionAppender = addAppender(logFilePath);
            logger.info("Redirected to file: " + logFilePath);
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    public void stopLoggingExtraction(final ExtractionId extractionId) {
        logger.info("Diagnostics finished redirecting for extraction: " + extractionId.toUniqueString());
        if (extractionAppender != null) {
            final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.blackducksoftware.integration");
            logbackLogger.detachAppender(extractionAppender);
            extractionAppender.stop();
        }
    }

    private FileAppender<ILoggingEvent> addAppender(final String file) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> appender;
        appender = new FileAppender<>();
        appender.setFile(file);
        appender.setEncoder(ple);
        appender.setContext(lc);
        appender.start();

        final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.blackducksoftware.integration");

        logbackLogger.addAppender(appender);
        logbackLogger.setLevel(Level.ALL);

        return appender;
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
