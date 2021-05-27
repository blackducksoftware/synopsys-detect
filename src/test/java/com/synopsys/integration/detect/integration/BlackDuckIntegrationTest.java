/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.CodeLocationService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.IntegrationEscapeUtil;

public abstract class BlackDuckIntegrationTest {
    public static final String BLACKDUCK_URL = "BLACKDUCK_URL";
    public static final String BLACKDUCK_API_TOKEN = "BLACKDUCK_API_TOKEN";

    protected static IntLogger logger;
    protected static BlackDuckServicesFactory blackDuckServicesFactory;
    protected static BlackDuckApiClient blackDuckService;
    protected static ProjectService projectService;
    protected static ProjectBomService projectBomService;
    protected static CodeLocationService codeLocationService;
    protected static ReportService reportService;
    protected static boolean previousShouldExit;

    @BeforeAll
    public static void setup() {
        logger = new BufferedIntLogger();

        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());
        blackDuckServerConfigBuilder.setUrl(System.getenv().get(BLACKDUCK_URL));
        blackDuckServerConfigBuilder.setApiToken(System.getenv().get(BLACKDUCK_API_TOKEN));
        blackDuckServerConfigBuilder.setTrustCert(true);
        blackDuckServerConfigBuilder.setTimeoutInSeconds(5 * 60);

        blackDuckServicesFactory = blackDuckServerConfigBuilder.build().createBlackDuckServicesFactory(logger);
        blackDuckService = blackDuckServicesFactory.getBlackDuckApiClient();
        projectService = blackDuckServicesFactory.createProjectService();
        projectBomService = blackDuckServicesFactory.createProjectBomService();
        codeLocationService = blackDuckServicesFactory.createCodeLocationService();

        Gson gson = blackDuckServicesFactory.getGson();
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
        ApiDiscovery apiDiscovery = blackDuckServicesFactory.getApiDiscovery();
        HttpUrl blackDuckUrl = blackDuckServicesFactory.getBlackDuckHttpClient().getBlackDuckUrl();
        IntegrationEscapeUtil integrationEscapeUtil = blackDuckServicesFactory.createIntegrationEscapeUtil();
        long reportServiceTimeout = 120 * 1000;
        reportService = new ReportService(gson, blackDuckUrl, blackDuckApiClient, apiDiscovery, logger, integrationEscapeUtil, reportServiceTimeout);

        previousShouldExit = Application.shouldExit();
        Application.setShouldExit(false);
    }

    @AfterAll
    public static void cleanup() {
        Application.setShouldExit(previousShouldExit);
    }

    public ProjectVersionWrapper assertProjectVersionReady(String projectName, String projectVersionName) throws IntegrationException {
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        if (optionalProjectVersionWrapper.isPresent()) {
            blackDuckService.delete(optionalProjectVersionWrapper.get().getProjectView());
        }

        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectName, projectVersionName);
        projectService.syncProjectAndVersion(projectSyncModel);
        optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        assertTrue(optionalProjectVersionWrapper.isPresent());

        List<CodeLocationView> codeLocations = blackDuckService.getAllResponses(optionalProjectVersionWrapper.get().getProjectVersionView().metaCodelocationsLink());
        assertEquals(0, codeLocations.size());

        List<ProjectVersionComponentView> bomComponents = projectBomService.getComponentsForProjectVersion(optionalProjectVersionWrapper.get().getProjectVersionView());
        assertEquals(0, bomComponents.size());

        return optionalProjectVersionWrapper.get();
    }

    public static List<String> getInitialArgs(String projectName, String projectVersionName) {
        List<String> initialArgs = new ArrayList<>();
        initialArgs.add("--detect.tools.excluded=POLARIS");
        initialArgs.add("--detect.project.name=" + projectName);
        initialArgs.add("--detect.project.version.name=" + projectVersionName);
        initialArgs.add("--blackduck.url=" + System.getenv().get(BLACKDUCK_URL));
        initialArgs.add("--blackduck.api.token=" + System.getenv().get(BLACKDUCK_API_TOKEN));

        return initialArgs;
    }

}
