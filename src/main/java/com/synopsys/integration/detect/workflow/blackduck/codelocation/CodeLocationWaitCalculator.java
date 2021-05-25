/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class CodeLocationWaitCalculator {
    public CodeLocationWaitData calculateWaitData(List<CodeLocationCreationData<? extends CodeLocationBatchOutput<?>>> codeLocationCreationDatas) {
        int expectedNotificationCount = 0;
        NotificationTaskRange notificationRange = null;
        Set<String> codeLocationNames = new HashSet<>();

        for (CodeLocationCreationData<? extends CodeLocationBatchOutput<?>> codeLocationCreationData : codeLocationCreationDatas) {
            expectedNotificationCount += codeLocationCreationData.getOutput().getExpectedNotificationCount();
            codeLocationNames.addAll(codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames());

            if (null == notificationRange) {
                notificationRange = codeLocationCreationData.getNotificationTaskRange();
            } else {
                NotificationTaskRange rangeToAdd = codeLocationCreationData.getNotificationTaskRange();
                long earliestTaskTime = Math.min(notificationRange.getTaskStartTime(), rangeToAdd.getTaskStartTime());
                Date earliestStartDate = earliestDate(notificationRange.getStartDate(), rangeToAdd.getStartDate());
                Date latestEndDate = latestDate(notificationRange.getEndDate(), rangeToAdd.getEndDate());
                notificationRange = new NotificationTaskRange(earliestTaskTime, earliestStartDate, latestEndDate);
            }
        }

        return new CodeLocationWaitData(notificationRange, codeLocationNames, expectedNotificationCount);
    }

    private Date earliestDate(Date d1, Date d2) {
        if (d1.before(d2)) {
            return d1;
        }
        return d2;
    }

    private Date latestDate(Date d1, Date d2) {
        if (d1.after(d2)) {
            return d1;
        }
        return d2;
    }
}
