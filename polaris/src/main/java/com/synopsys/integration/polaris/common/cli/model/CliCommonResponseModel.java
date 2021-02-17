/*
 * polaris
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
package com.synopsys.integration.polaris.common.cli.model;

import java.util.List;
import java.util.Optional;

public class CliCommonResponseModel {
    private CommonScanInfo scanInfo;
    private CommonProjectInfo projectInfo;
    private CommonIssueSummary issueSummary;
    private List<CommonToolInfo> tools;

    public CommonScanInfo getScanInfo() {
        return scanInfo;
    }

    public void setScanInfo(final CommonScanInfo scanInfo) {
        this.scanInfo = scanInfo;
    }

    public CommonProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(final CommonProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public Optional<CommonIssueSummary> getIssueSummary() {
        return Optional.ofNullable(issueSummary);
    }

    public void setIssueSummary(final CommonIssueSummary issueSummary) {
        this.issueSummary = issueSummary;
    }

    public List<CommonToolInfo> getTools() {
        return tools;
    }

    public void setTools(final List<CommonToolInfo> tools) {
        this.tools = tools;
    }

}
