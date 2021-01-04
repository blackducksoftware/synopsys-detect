/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
