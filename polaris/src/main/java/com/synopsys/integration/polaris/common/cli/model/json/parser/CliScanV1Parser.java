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
package com.synopsys.integration.polaris.common.cli.model.json.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.cli.model.CommonToolInfo;
import com.synopsys.integration.polaris.common.cli.model.json.v1.CliScanV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ToolInfoV1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CliScanV1Parser extends CliScanParser<CliScanV1> {
    public CliScanV1Parser(Gson gson) {
        super(gson);
    }

    @Override
    public TypeToken<CliScanV1> getTypeToken() {
        return new TypeToken<CliScanV1>() {
        };
    }

    @Override
    public CliCommonResponseModel fromCliScan(JsonObject versionlessModel) {
        CliScanV1 cliScanV1 = fromJson(versionlessModel);
        final CliCommonResponseModel cliCommonResponseModel = createResponseModel(cliScanV1.issueSummary, cliScanV1.projectInfo, cliScanV1.scanInfo);

        final List<CommonToolInfo> tools = new ArrayList<>();
        //TODO verify case of tool names
        fromToolInfoV1(cliScanV1.blackDuckScaToolInfo, "sca", tools::add);
        fromToolInfoV1(cliScanV1.coverityToolInfo, "Coverity", tools::add);

        cliCommonResponseModel.setTools(tools);

        return cliCommonResponseModel;

    }

    private void fromToolInfoV1(final ToolInfoV1 toolInfoV1, final String toolName, Consumer<CommonToolInfo> consumer) {
        if (toolInfoV1 != null) {
            CommonToolInfo commonToolInfo = createCommonToolInfo(toolInfoV1);
            commonToolInfo.setToolName(toolName);

            consumer.accept(commonToolInfo);
        }
    }

}
