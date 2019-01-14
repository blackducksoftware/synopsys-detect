/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.tool.bazel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.tool.ToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class BazelToolResult implements ToolResult {
    public enum BazelToolResultType {
        SUCCESS,
        FAILURE,
        SKIPPED
    }

    public Optional<NameVersion> bazelProjectNameVersion = Optional.empty();
    public List<DetectCodeLocation> bazelCodeLocations = new ArrayList<>();

    public BazelToolResult.BazelToolResultType resultType = BazelToolResult.BazelToolResultType.SUCCESS;
    public String errorMessage;

    @Override
    public BazelToolResult failure(String message) {
        BazelToolResult result = new BazelToolResult();
        result.resultType = BazelToolResult.BazelToolResultType.FAILURE;
        result.errorMessage = message;
        return result;
    }

    @Override
    public BazelToolResult skipped() {
        BazelToolResult result = new BazelToolResult();
        result.resultType = BazelToolResult.BazelToolResultType.SKIPPED;
        return result;
    }
}
