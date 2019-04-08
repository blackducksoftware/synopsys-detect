/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.hub;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class CodeLocationWaitData {
    private NotificationTaskRange notificationRange;
    private Set<String> codeLocationNames = new HashSet<>();
    private int expectedNotificationCount = 0;

    public void addWaitForCreationData(CodeLocationCreationData codeLocationCreationData) {
        expectedNotificationCount += codeLocationCreationData.getOutput().getExpectedNotificationCount();
        codeLocationNames.addAll(codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames());
        if (null == notificationRange) {
            notificationRange = codeLocationCreationData.getNotificationTaskRange();
        } else {
            NotificationTaskRange rangeToAdd = codeLocationCreationData.getNotificationTaskRange();
            long earliestTaskTime = Math.min(notificationRange.getTaskStartTime(), rangeToAdd.getTaskStartTime());
            Date earliestStartDate = earliestDate(notificationRange.getStartDate(), rangeToAdd.getStartDate());
            Date latestEndDate = latestDate(notificationRange.getEndDate(), rangeToAdd.getEndDate());
            this.notificationRange = new NotificationTaskRange(earliestTaskTime, earliestStartDate, latestEndDate);
        }
    }

    private Date earliestDate(Date d1, Date d2) {
        if (d1.before(d2))
            return d1;
        return d2;
    }

    private Date latestDate(Date d1, Date d2) {
        if (d1.after(d2))
            return d1;
        return d2;
    }

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
