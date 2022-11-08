package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detect.workflow.event.EventSystem;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public class DiagnosticLogSystem {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiagnosticSysOutCapture diagnosticSysOutCapture;
    private final List<DiagnosticLogger> loggers = new ArrayList<>();
    private DiagnosticLogger extractionLogger = null;
    private final File logDirectory;

    public DiagnosticLogSystem(File logDirectory, EventSystem eventSystem) {
        //The intent of diagnostics logging is to:
        //Capture whatever is written to Sys Out regardless of actual logging level.
        //Set the our code's CONSOLE output to the level of DEBUG.
        //Also capture our logs written to TRACE, DEBUG and INFO into separate log files.
        //Our logs refers to com.synopsys.integration

        this.logDirectory = logDirectory;

        logger.info("Attempting to capture sysout.");
        diagnosticSysOutCapture = new DiagnosticSysOutCapture(logFileNamed("sysout"));
        diagnosticSysOutCapture.startCapture();

        logger.info("Attempting to set 'com.synopsys.integration' logging level.");
        DiagnosticLogUtil.getOurLogger().setLevel(Level.ALL);

        logger.info("Attempting to capture additional log messages to files.");
        addLoggers(Level.ALL, Level.DEBUG, Level.INFO);

        logger.info("Attempting to restrict console to debug level (additional levels written to files).");
        restrictConsoleToDebug();

        logger.info("Adding additional log listeners to extractions.");
        //TODO (detectors): Add the detectors back in.
        //        eventSystem.registerListener(Event.ExtractionStarted, it -> startLoggingExtraction(((DetectExtractionEnvironment) it.getExtractionEnvironment()).getExtractionId()));
        //        eventSystem.registerListener(Event.ExtractionEnded, it -> stopLoggingExtraction(((DetectExtractionEnvironment) it.getExtractionEnvironment()).getExtractionId()));

        logger.info("Diagnostics is now in control of logging!");
    }

    public void startLoggingExtraction(ExtractionId extractionId) {
        logger.info("Diagnostics attempting to redirect extraction logs: " + extractionId.toUniqueString());
        File logDir = new File(logDirectory, "extractions");
        logDir.mkdirs();
        File logFile = new File(logDir, extractionId.toUniqueString() + ".txt");
        extractionLogger = new DiagnosticLogger(logFile, Level.ALL);
        extractionLogger.startLogging();
    }

    public void stopLoggingExtraction(ExtractionId extractionId) {
        logger.info("Diagnostics finished redirecting for extraction: " + extractionId.toUniqueString());
        if (extractionLogger != null) {
            extractionLogger.stopLogging();
        }
    }

    private void restrictConsoleToDebug() {
        for (Iterator<Appender<ILoggingEvent>> it = DiagnosticLogUtil.getRootLogger().iteratorForAppenders(); it.hasNext(); ) {
            Appender appender = it.next();
            if (appender.getName() != null && "CONSOLE".equals(appender.getName())) {
                ThresholdFilter levelFilter = new ThresholdFilter();
                levelFilter.setLevel(Level.DEBUG.levelStr);
                levelFilter.start();
                appender.addFilter(levelFilter);
            }
        }
    }

    private File logFileNamed(String name) {
        return new File(logDirectory, name + ".txt");
    }

    public void finish() {
        diagnosticSysOutCapture.stopCapture();
        for (DiagnosticLogger diagnosticLogger : loggers) {
            diagnosticLogger.stopLogging();
        }
    }

    private void addLogger(Level level) {
        File logFile = logFileNamed(level.levelStr.toLowerCase());
        DiagnosticLogger diagnosticLogger = new DiagnosticLogger(logFile, level);
        loggers.add(diagnosticLogger);
        diagnosticLogger.startLogging();
    }

    private void addLoggers(Level... levels) {
        for (Level level : levels) {
            addLogger(level);
        }
    }
}
