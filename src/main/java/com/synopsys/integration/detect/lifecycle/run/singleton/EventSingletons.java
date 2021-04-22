/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;

public class EventSingletons {
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectorEventPublisher detectorEventPublisher;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final ProjectEventPublisher projectEventPublisher;

    public EventSingletons(final StatusEventPublisher statusEventPublisher, final ExitCodePublisher exitCodePublisher, final DetectorEventPublisher detectorEventPublisher,
        final CodeLocationEventPublisher codeLocationEventPublisher, final ProjectEventPublisher projectEventPublisher) {
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
