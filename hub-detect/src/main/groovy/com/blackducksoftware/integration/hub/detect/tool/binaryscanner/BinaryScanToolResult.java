package com.blackducksoftware.integration.hub.detect.tool.binaryscanner;

import java.util.Set;

import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class BinaryScanToolResult {
    private final NotificationTaskRange notificationTaskRange;
    private final Set<String> codeLocationNames;
    private final boolean successful;

    public BinaryScanToolResult(final NotificationTaskRange notificationTaskRange, final Set<String> codeLocationNames, final boolean successful) {
        this.notificationTaskRange = notificationTaskRange;
        this.codeLocationNames = codeLocationNames;
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public NotificationTaskRange getNotificationTaskRange() {
        return notificationTaskRange;
    }

    public Set<String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
