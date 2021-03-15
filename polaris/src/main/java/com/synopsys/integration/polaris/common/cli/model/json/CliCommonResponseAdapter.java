/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
