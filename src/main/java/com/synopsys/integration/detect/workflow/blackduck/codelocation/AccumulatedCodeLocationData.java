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
