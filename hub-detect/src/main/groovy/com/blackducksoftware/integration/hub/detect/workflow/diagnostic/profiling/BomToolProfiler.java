package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;

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

    public List<BomToolTime> getApplicableTimings() {
        return applicableTimekeeper.getTimings();
    }

    public List<BomToolTime> getExtractableTimings() {
        return extractableTimekeeper.getTimings();
    }

    public List<BomToolTime> getExtractionTimings() {
        return extractionTimekeeper.getTimings();
    }

    public Map<BomToolGroupType, Long> getAggregateBomToolGroupTimes() {
        final Map<BomToolGroupType, Long> aggregate = new HashMap<>();
        addAggregateByBomToolGroupType(aggregate, getExtractableTimings());
        addAggregateByBomToolGroupType(aggregate, getExtractionTimings());
        return aggregate;
    }

    void addAggregateByBomToolGroupType(final Map<BomToolGroupType, Long> aggregate, final List<BomToolTime> bomToolTimes) {
        for (final BomToolTime bomToolTime : bomToolTimes) {
            final BomToolGroupType type = bomToolTime.getBomTool().getBomToolGroupType();
            if (!aggregate.containsKey(type)) {
                aggregate.put(type, 0L);
            }
            final long time = bomToolTime.getMs();
            final Long currentTime = aggregate.get(type);
            final Long sum = time + currentTime;
            aggregate.put(type, sum);
        }
    }

}
