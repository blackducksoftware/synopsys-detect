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
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class CodeLocationWaitData {
    private NotificationTaskRange bdioUploadRange;
    private Set<String> bdioUploadCodeLocationNames;
    private boolean hasBdioResults;

    private NotificationTaskRange signatureScanRange;
    private Set<String> signatureScanCodeLocationNames;
    private boolean hasScanResults;

    private NotificationTaskRange binaryScanRange;
    private Set<String> binaryScanCodeLocationNames;
    private boolean hasBinaryResults;

    public void setFromBdioCodeLocationCreationData(CodeLocationCreationData<UploadBatchOutput> bdioCodeLocationCreationData) {
        bdioUploadRange = bdioCodeLocationCreationData.getNotificationTaskRange();
        bdioUploadCodeLocationNames = bdioCodeLocationCreationData.getOutput().getSuccessfulCodeLocationNames();
        hasBdioResults = true;
    }

    public void setFromSignatureScannerCodeLocationCreationData(CodeLocationCreationData<ScanBatchOutput> scanCodeLocationCreationData) {
        signatureScanRange = scanCodeLocationCreationData.getNotificationTaskRange();
        signatureScanCodeLocationNames = scanCodeLocationCreationData.getOutput().getSuccessfulCodeLocationNames();
        hasScanResults = true;
    }

    public void setFromBinaryScan(NotificationTaskRange notificationTaskRange, Set<String> codeLocationNames) {
        binaryScanRange = notificationTaskRange;
        binaryScanCodeLocationNames = codeLocationNames;
        hasBinaryResults = true;
    }

    public NotificationTaskRange getBdioUploadRange() {
        return bdioUploadRange;
    }

    public Set<String> getBdioUploadCodeLocationNames() {
        return bdioUploadCodeLocationNames;
    }

    public boolean hasBdioResults() {
        return hasBdioResults;
    }

    public NotificationTaskRange getSignatureScanRange() {
        return signatureScanRange;
    }

    public Set<String> getSignatureScanCodeLocationNames() {
        return signatureScanCodeLocationNames;
    }

    public boolean hasScanResults() {
        return hasScanResults;
    }

    public NotificationTaskRange getBinaryScanRange() {
        return binaryScanRange;
    }

    public Set<String> getBinaryScanCodeLocationNames() {
        return binaryScanCodeLocationNames;
    }

    public boolean hasBinaryScanResults() {
        return hasBinaryResults;
    }

}
