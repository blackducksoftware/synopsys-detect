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
package com.synopsys.integration.detect.workflow.bdio;

import java.util.List;

import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;

public class BdioResult {
    private final List<UploadTarget> uploadTargets;
    private final DetectCodeLocationNamesResult codeLocationNamesResult;
    private final boolean isBdio2;

    public BdioResult(final List<UploadTarget> uploadTargets, final DetectCodeLocationNamesResult codeLocationNamesResult, final boolean isBdio2) {
        this.uploadTargets = uploadTargets;
        this.codeLocationNamesResult = codeLocationNamesResult;
        this.isBdio2 = isBdio2;
    }

    public List<UploadTarget> getUploadTargets() {
        return uploadTargets;
    }

    public boolean isBdio2() {
        return isBdio2;
    }

    public DetectCodeLocationNamesResult getCodeLocationNamesResult() {
        return codeLocationNamesResult;
    }
}
