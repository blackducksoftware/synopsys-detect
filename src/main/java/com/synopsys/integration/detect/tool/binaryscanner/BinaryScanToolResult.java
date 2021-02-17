/*
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
