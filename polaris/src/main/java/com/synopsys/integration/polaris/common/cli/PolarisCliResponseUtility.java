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
package com.synopsys.integration.polaris.common.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.cli.model.json.CliCommonResponseAdapter;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;

public class PolarisCliResponseUtility {
    private final IntLogger logger;
    private final Gson gson;
    private final CliCommonResponseAdapter cliCommonResponseAdapter;

    public PolarisCliResponseUtility(final IntLogger logger, final Gson gson) {
        this.logger = logger;
        this.gson = gson;
        cliCommonResponseAdapter = new CliCommonResponseAdapter(gson);
    }

    public static PolarisCliResponseUtility defaultUtility(final IntLogger logger) {
        return new PolarisCliResponseUtility(logger, new Gson());
    }

    public static Path getDefaultPathToJson(final String projectRootDirectory) {
        return Paths.get(projectRootDirectory)
                   .resolve(".synopsys")
                   .resolve("polaris")
                   .resolve("cli-scan.json");
    }

    public Gson getGson() {
        return gson;
    }

    public CliCommonResponseModel getPolarisCliResponseModelFromDefaultLocation(final String projectRootDirectory) throws PolarisIntegrationException {
        final Path pathToJson = getDefaultPathToJson(projectRootDirectory);
        return getPolarisCliResponseModel(pathToJson);
    }

    public CliCommonResponseModel getPolarisCliResponseModel(final String pathToJson) throws PolarisIntegrationException {
        final Path actualPathToJson = Paths.get(pathToJson);
        return getPolarisCliResponseModel(actualPathToJson);
    }

    public CliCommonResponseModel getPolarisCliResponseModel(final Path pathToJson) throws PolarisIntegrationException {
        try (BufferedReader reader = Files.newBufferedReader(pathToJson)) {
            logger.debug("Attempting to retrieve CliCommonResponseModel from " + pathToJson.toString());
            return getPolarisCliResponseModelFromJsonObject(gson.fromJson(reader, JsonObject.class));
        } catch (final IOException e) {
            throw new PolarisIntegrationException("There was a problem parsing the Polaris CLI response json at " + pathToJson.toString(), e);
        }
    }

    public CliCommonResponseModel getPolarisCliResponseModelFromString(final String rawPolarisCliResponse) throws PolarisIntegrationException {
        return getPolarisCliResponseModelFromJsonObject(gson.fromJson(rawPolarisCliResponse, JsonObject.class));
    }

    public CliCommonResponseModel getPolarisCliResponseModelFromJsonObject(final JsonObject versionlessModel) throws PolarisIntegrationException {
        final String versionString = versionlessModel.get("version").getAsString();
        final PolarisCliResponseVersion polarisCliResponseVersion = PolarisCliResponseVersion.parse(versionString)
                                                                        .orElseThrow(() -> new PolarisIntegrationException("Version " + versionString + " is not a valid version of cli-scan.json"));

        return cliCommonResponseAdapter.fromJson(versionString, polarisCliResponseVersion, versionlessModel);
    }

}
