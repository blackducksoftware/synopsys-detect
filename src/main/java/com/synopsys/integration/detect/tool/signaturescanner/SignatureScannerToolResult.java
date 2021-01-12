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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;

public class SignatureScannerToolResult {
    private final Optional<CodeLocationCreationData<ScanBatchOutput>> creationData;
    private final ScanBatchOutput scanBatchOutput;
    private final Result result;

    public static SignatureScannerToolResult createOnlineResult(CodeLocationCreationData<ScanBatchOutput> creationData) {
        return new SignatureScannerToolResult(creationData, creationData.getOutput(), Result.SUCCESS);
    }

    public static SignatureScannerToolResult createOfflineResult(ScanBatchOutput scanBatchOutput) {
        return new SignatureScannerToolResult(null, scanBatchOutput, Result.SUCCESS);
    }

    public static SignatureScannerToolResult createFailureResult() {
        return new SignatureScannerToolResult(null, null, Result.FAILURE);
    }

    private SignatureScannerToolResult(final CodeLocationCreationData<ScanBatchOutput> creationData, final ScanBatchOutput scanBatchOutput, Result result) {
        this.creationData = Optional.ofNullable(creationData);
        this.scanBatchOutput = scanBatchOutput;
        this.result = result;
    }

    public Optional<CodeLocationCreationData<ScanBatchOutput>> getCreationData() {
        return creationData;
    }

    public ScanBatchOutput getScanBatchOutput() {
        return scanBatchOutput;
    }

    public Result getResult() {
        return result;
    }

}
