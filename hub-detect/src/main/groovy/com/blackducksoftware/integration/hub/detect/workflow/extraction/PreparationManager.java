package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExceptionBomToolResult;

public class PreparationManager {
    private final EventSystem eventSystem;

    public PreparationManager(final EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    private void prepare(final BomToolEvaluation result) {
        if (result.isApplicable()) {
            eventSystem.publishEvent(Event.ExtractableStarted, result.getDetector());
            try {
                result.setExtractable(result.getDetector().extractable());
            } catch (final Exception e) {
                result.setExtractable(new ExceptionBomToolResult(e));
            }
            eventSystem.publishEvent(Event.ExtractableEnded, result.getDetector());
        }
    }

    public PreparationResult prepareExtractions(final List<BomToolEvaluation> results) {
        for (final BomToolEvaluation result : results) {
            prepare(result);
        }

        final Set<DetectorType> succesfulBomToolGroups = results.stream()
                                                             .filter(it -> it.isApplicable())
                                                             .filter(it -> it.isExtractable())
                                                             .map(it -> it.getDetector().getDetectorType())
                                                             .collect(Collectors.toSet());

        final Set<DetectorType> failedBomToolGroups = results.stream()
                                                          .filter(it -> it.isApplicable())
                                                          .filter(it -> !it.isExtractable())
                                                          .map(it -> it.getDetector().getDetectorType())
                                                          .collect(Collectors.toSet());

        return new PreparationResult(succesfulBomToolGroups, failedBomToolGroups, results);
    }
}
