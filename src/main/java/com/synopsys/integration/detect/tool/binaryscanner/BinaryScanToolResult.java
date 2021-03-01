/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.binaryscanner;

import java.util.Collections;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class BinaryScanToolResult {
    private final CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData;

    public static BinaryScanToolResult SUCCESS(CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData) {
        return new BinaryScanToolResult(codeLocationCreationData);
    }

    public static BinaryScanToolResult FAILURE() {
        return new BinaryScanToolResult(null);
    }

    private BinaryScanToolResult(CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData) {
        this.codeLocationCreationData = codeLocationCreationData;
    }

    public boolean isSuccessful() {
        if (null == codeLocationCreationData) {
            return false;
        }
        return codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames().size() > 0;
    }

    public NotificationTaskRange getNotificationTaskRange() {
        if (null == codeLocationCreationData) {
            return null;
        }
        return codeLocationCreationData.getNotificationTaskRange();
    }

    public Set<String> getCodeLocationNames() {
        if (null == codeLocationCreationData) {
            return Collections.emptySet();
        }
        return codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames();
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> getCodeLocationCreationData() {
        return codeLocationCreationData;
    }

}
