package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;

public class EventSingletons {
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectorEventPublisher detectorEventPublisher;
    private final ProjectEventPublisher projectEventPublisher;

    public EventSingletons(
        StatusEventPublisher statusEventPublisher,
        ExitCodePublisher exitCodePublisher,
        DetectorEventPublisher detectorEventPublisher,
        ProjectEventPublisher projectEventPublisher
    ) {
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.detectorEventPublisher = detectorEventPublisher;
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

    public ProjectEventPublisher getProjectEventPublisher() {
        return projectEventPublisher;
    }
}
