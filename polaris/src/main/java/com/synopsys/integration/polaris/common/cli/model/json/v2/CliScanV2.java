/**
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
package com.synopsys.integration.polaris.common.cli.model.json.v2;

import com.synopsys.integration.polaris.common.cli.model.json.CliScanResponse;
import com.synopsys.integration.polaris.common.cli.model.json.v1.IssueSummaryV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ProjectInfoV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ScanInfoV1;

import java.util.List;

public class CliScanV2 implements CliScanResponse {
    public String version;
    public ScanInfoV1 scanInfo;
    public ProjectInfoV1 projectInfo;
    public IssueSummaryV1 issueSummary;
    public List<ToolInfoV2> tools;

}
