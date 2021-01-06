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
package com.synopsys.integration.polaris.common.cli.model.json.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.cli.model.CommonToolInfo;
import com.synopsys.integration.polaris.common.cli.model.json.v2.CliScanV2;
import com.synopsys.integration.polaris.common.cli.model.json.v2.ToolInfoV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CliScanV2Parser extends CliScanParser<CliScanV2> {
    public CliScanV2Parser(Gson gson) {
        super(gson);
    }

    @Override
    public TypeToken<CliScanV2> getTypeToken() {
        return new TypeToken<CliScanV2>() {
        };
    }

    @Override
    public CliCommonResponseModel fromCliScan(JsonObject versionlessModel) {
        CliScanV2 cliScanV2 = fromJson(versionlessModel);

        final CliCommonResponseModel cliCommonResponseModel = createResponseModel(cliScanV2.issueSummary, cliScanV2.projectInfo, cliScanV2.scanInfo);

        final List<CommonToolInfo> tools = new ArrayList<>();
        Optional.ofNullable(cliScanV2.tools)
                .orElse(Collections.emptyList())
                .stream()
                .forEach(tool -> fromToolInfoV2(tool, tools::add));

        cliCommonResponseModel.setTools(tools);

        return cliCommonResponseModel;
    }

    private void fromToolInfoV2(final ToolInfoV2 toolInfoV2, Consumer<CommonToolInfo> consumer) {
        if (toolInfoV2 != null) {
            CommonToolInfo commonToolInfo = createCommonToolInfo(toolInfoV2);
            commonToolInfo.setToolName(toolInfoV2.toolName);
            commonToolInfo.setIssueApiUrl(toolInfoV2.issueApiUrl);

            consumer.accept(commonToolInfo);
        }
    }

}
