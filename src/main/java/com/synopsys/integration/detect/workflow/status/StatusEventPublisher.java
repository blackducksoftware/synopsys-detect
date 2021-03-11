/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.detect.workflow.result.DetectResult;

public interface StatusEventPublisher {
    void publisStatusSummary(Status status);

    void publishIssue(DetectIssue issue);

    void publishDetectResult(DetectResult detectResult);
}
