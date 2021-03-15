/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class CodeLocationWaitData {
    @Nullable
    private final NotificationTaskRange notificationRange;
    private final Set<String> codeLocationNames;
    private int expectedNotificationCount;

    public CodeLocationWaitData(@Nullable final NotificationTaskRange notificationRange, final Set<String> codeLocationNames, final int expectedNotificationCount) {
        this.notificationRange = notificationRange;
        this.codeLocationNames = codeLocationNames;
        this.expectedNotificationCount = expectedNotificationCount;
    }

    @Nullable
    public NotificationTaskRange getNotificationRange() {
        return notificationRange;
    }

    public Set<String> getCodeLocationNames() {
        return codeLocationNames;
    }

    public int getExpectedNotificationCount() {
        return expectedNotificationCount;
    }

}
