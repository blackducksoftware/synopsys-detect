package com.blackduck.integration.detect.lifecycle.run.singleton;

import com.blackduck.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.blackduck.integration.detect.tool.detector.DetectorEventPublisher;
import com.blackduck.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.blackduck.integration.detect.workflow.project.ProjectEventPublisher;
import com.blackduck.integration.detect.workflow.status.StatusEventPublisher;

public class EventSingletons {
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectorEventPublisher detectorEventPublisher;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final ProjectEventPublisher projectEventPublisher;

    public EventSingletons(
        StatusEventPublisher statusEventPublisher,
        ExitCodePublisher exitCodePublisher,
        DetectorEventPublisher detectorEventPublisher,
        CodeLocationEventPublisher codeLocationEventPublisher,
        ProjectEventPublisher projectEventPublisher
    ) {
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.detectorEventPublisher = detectorEventPublisher;
        this.codeLocationEventPublisher = codeLocationEventPublisher;
        this.projectEventPublisher = projectEventPublisher;
    }

    public StatusEventPublisher getStatusEventPublisher() {
        return statusEventPublisher;
    }

    public ExitCodePublisher getExitCodePublisher() {
        return exitCodePublisher;
    }

    public DetectorEventPublisher getDetectorEventPublisher() {
        return detectorEventPublisher;
    }

    public CodeLocationEventPublisher getCodeLocationEventPublisher() {
        return codeLocationEventPublisher;
    }

    public ProjectEventPublisher getProjectEventPublisher() {
        return projectEventPublisher;
    }
}
