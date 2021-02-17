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
package com.synopsys.integration.polaris.common.cli.model.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synopsys.integration.polaris.common.cli.PolarisCliResponseVersion;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.cli.model.json.parser.*;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;

public class CliCommonResponseAdapter {
    private final Gson gson;

    public CliCommonResponseAdapter(final Gson gson) {
        this.gson = gson;
    }

    public CliCommonResponseModel fromJson(String versionString, PolarisCliResponseVersion polarisCliResponseVersion, final JsonObject versionlessModel) throws PolarisIntegrationException {
        CliScanParser<? extends CliScanResponse> cliScanParser = new CliScanUnsupportedParser(gson, versionString);

        final int majorVersion = polarisCliResponseVersion.getMajor();
        if (majorVersion == 1) {
            cliScanParser = new CliScanV1Parser(gson);
        } else if (majorVersion == 2) {
            cliScanParser = new CliScanV2Parser(gson);
        }

        return cliScanParser.fromCliScan(versionlessModel);
    }

}
