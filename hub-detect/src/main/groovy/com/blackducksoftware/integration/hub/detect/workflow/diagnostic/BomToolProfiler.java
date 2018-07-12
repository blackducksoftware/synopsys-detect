package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class BomToolProfiler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BomToolTimekeeper applicableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractionTimekeeper = new BomToolTimekeeper();

    public void applicableStarted(final BomTool bomTool) {
        applicableTimekeeper.started(bomTool);
    }

    public void applicableEnded(final BomTool bomTool) {
        applicableTimekeeper.ended(bomTool);
    }

    public void extractableStarted(final BomTool bomTool) {
        extractableTimekeeper.started(bomTool);
    }

    public void extractableEnded(final BomTool bomTool) {
        extractableTimekeeper.ended(bomTool);
    }

    public void extractionStarted(final BomTool bomTool) {
        extractionTimekeeper.started(bomTool);
    }

    public void extractionEnded(final BomTool bomTool) {
        extractionTimekeeper.ended(bomTool);
    }

    List<BomToolTime> getApplicableTimings() {
        return applicableTimekeeper.getTimings();
    }

    List<BomToolTime> getExtractableTimings() {
        return extractableTimekeeper.getTimings();
    }

    List<BomToolTime> getExtractionTimings() {
        return extractionTimekeeper.getTimings();
    }

    public void writeToLogs() {
        logger.info("Bom Tool Applicable Times:");
        writeToLogs(aggregateByName(applicableTimekeeper));
        logger.info("Bom Tool Extractable Times:");
        writeToLogs(extractableTimekeeper);
        logger.info("Bom Tool Extraction Times:");
        writeToLogs(extractionTimekeeper);
    }

    private void writeToLogs(final BomToolTimekeeper timekeeper) {
        for (final BomToolTime time : timekeeper.getTimings()) {
            logger.info("\tBom Tool: " + time.getBomTool().getDescriptiveName());
            logger.info("\t\tTook: " + time.getMs());
        }
    }

    private void writeToLogs(final Map<String, Long> timeMap) {
        for (final String key : timeMap.keySet()) {
            logger.info("\tBom Tool: " + key);
            logger.info("\t\tTook: " + timeMap.get(key));
        }
    }

    private Map<String, Long> aggregateByName(final BomToolTimekeeper timekeeper) {
        final Map<String, Long> timeMap = new HashMap<>();
        for (final BomToolTime time : timekeeper.getTimings()) {
            final String key = time.getBomTool().getDescriptiveName();
            Long value = (long) 0;
            if (timeMap.containsKey(key)) {
                value = timeMap.get(key);
            }
            value += time.getMs();
            timeMap.put(key, value);
        }
        return timeMap;
    }
}
