package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.util.List;

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

}
