package com.blackducksoftware.integration.hub.detect.diagnostic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.ExtractionContext;


@Component
public class Profiler {
    private final Logger logger = LoggerFactory.getLogger(Profiler.class);

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
        for (final ExtractionContext context : stopwatchMap.keySet()) {
            final StopWatch sw = stopwatchMap.get(context);
            logger.debug("Extraction took: " + sw.getTime());
        }
    }

}
