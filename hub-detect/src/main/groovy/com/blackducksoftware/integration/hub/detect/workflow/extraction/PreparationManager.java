package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExceptionBomToolResult;

public class PreparationManager {
    private final EventSystem eventSystem;

    public PreparationManager(final EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    private void prepare(final BomToolEvaluation result) {
        if (result.isApplicable()) {
            eventSystem.publishEvent(Event.ExtractableStarted, result.getBomTool());
            try {
                result.setExtractable(result.getBomTool().extractable());
            } catch (final Exception e) {
                result.setExtractable(new ExceptionBomToolResult(e));
            }
            eventSystem.publishEvent(Event.ExtractableEnded, result.getBomTool());
        }
    }

    public PreparationResult prepareExtractions(final List<BomToolEvaluation> results) {
        for (final BomToolEvaluation result : results) {
            prepare(result);
        }

        final Set<BomToolGroupType> succesfulBomToolGroups = results.stream()
                                                                 .filter(it -> it.isApplicable())
                                                                 .filter(it -> it.isExtractable())
                                                                 .map(it -> it.getBomTool().getBomToolGroupType())
                                                                 .collect(Collectors.toSet());

        final Set<BomToolGroupType> failedBomToolGroups = results.stream()
                                                              .filter(it -> it.isApplicable())
                                                              .filter(it -> !it.isExtractable())
                                                              .map(it -> it.getBomTool().getBomToolGroupType())
                                                              .collect(Collectors.toSet());

        return new PreparationResult(succesfulBomToolGroups, failedBomToolGroups);
    }
}
