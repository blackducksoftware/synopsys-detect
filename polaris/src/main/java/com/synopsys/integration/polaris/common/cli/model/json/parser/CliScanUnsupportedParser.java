/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model.json.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.cli.model.json.UnsupportedVersionCliScanResponse;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;

public class CliScanUnsupportedParser extends CliScanParser<UnsupportedVersionCliScanResponse> {
    private final String versionString;

    public CliScanUnsupportedParser(Gson gson, String versionString) {
        super(gson);
        this.versionString = versionString;
    }

    @Override
    public TypeToken<UnsupportedVersionCliScanResponse> getTypeToken() {
        return null;
    }

    @Override
    public CliCommonResponseModel fromCliScan(JsonObject versionlessModel) throws PolarisIntegrationException {
        throw new PolarisIntegrationException("Version " + versionString + " of the cli-scan.json is not supported.");
    }

}
