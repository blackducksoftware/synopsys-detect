package com.blackducksoftware.integration.hub.detect.diagnostic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager.ReportTypes;
import com.blackducksoftware.integration.hub.detect.extraction.model.ExtractionContext;


@Component
public class Profiler {
    private final Logger logger = LoggerFactory.getLogger(Profiler.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

    public Map<ExtractionContext, StopWatch> stopwatchMap = new HashMap<>();

    public void startedExtraction(final ExtractionContext context) {
        final StopWatch sw = new StopWatch();
        sw.start();

        stopwatchMap.put(context, sw);
    }

    public void endedExtraction(final ExtractionContext context) {
        final StopWatch sw = stopwatchMap.get(context);
        sw.stop();
    }

    public void reportToLog() {
        final Map<Class, Long> timeByClass = new HashMap<>();
        Long totalTime = 0l;

        final DiagnosticReportWriter writer = diagnosticsManager.getReportWriter(ReportTypes.extractionProfile);
        for (final ExtractionContext context : stopwatchMap.keySet()) {
            final StopWatch sw = stopwatchMap.get(context);
            final long elapsed = sw.getTime();
            final Class clazz = context.getClass();
            writer.writeLine("Extraction: " + clazz.getSimpleName());
            writer.writeLine("Extraction Id: " + context.hashCode());
            writer.writeLine("Time Elapsed: " + elapsed);
            writer.writeLine("");
            totalTime += elapsed;
            if (!timeByClass.containsKey(clazz)) {
                timeByClass.put(clazz, 0l);
            }
            Long time = timeByClass.get(clazz);
            time += elapsed;
            timeByClass.put(clazz, time);
        }

        writer.writeLine("");
        writer.writeLine("Total Times by Class");
        writer.writeLine("");
        for (final Class clazz : timeByClass.keySet()) {
            final Long elapsed = timeByClass.get(clazz);
            writer.writeLine("Extraction Class: " + clazz.getSimpleName());
            writer.writeLine("Time Elapsed: " + elapsed);
        }
        writer.writeLine("");
        writer.writeLine("Total Time Elapsed: " + totalTime);
    }

}
