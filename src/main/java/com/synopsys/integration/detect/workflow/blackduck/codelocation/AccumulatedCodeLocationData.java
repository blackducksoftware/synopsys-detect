/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.Set;

import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class AccumulatedCodeLocationData {
    private final int expectedNotificationCount;
    private final Set<String> successfulCodeLocationNames;
    private final NotificationTaskRange notificationTaskRange;

    public AccumulatedCodeLocationData(final int expectedNotificationCount, final Set<String> successfulCodeLocationNames, final NotificationTaskRange notificationTaskRange) {
        this.expectedNotificationCount = expectedNotificationCount;
        this.successfulCodeLocationNames = successfulCodeLocationNames;
        this.notificationTaskRange = notificationTaskRange;
    }

    public int getExpectedNotificationCount() {
        return expectedNotificationCount;
    }

    public Set<String> getSuccessfulCodeLocationNames() {
        return successfulCodeLocationNames;
    }

    public NotificationTaskRange getNotificationTaskRange() {
        return notificationTaskRange;
    }
}
