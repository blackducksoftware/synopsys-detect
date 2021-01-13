/**
 * buildSrc
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
package com.synopsys.integration.detect.verification;

import java.util.List;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.PrintStreamIntLogger;

public class VerifyBlackDuckDetectTask extends DefaultTask {
    @TaskAction
    public void verifyBlackDuckAction() throws IntegrationException {
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newBuilder();
        Set<String> environmentKeys = blackDuckServerConfigBuilder.getEnvironmentVariableKeys();
        environmentKeys.forEach(it -> {
            String value = System.getenv().get(it);
            if (value != null) {
                blackDuckServerConfigBuilder.setProperty(it, value);
            }
        });

        BlackDuckServerConfig blackDuckServerConfig = blackDuckServerConfigBuilder.build();
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckServerConfig.createBlackDuckServicesFactory(new PrintStreamIntLogger(System.out, com.synopsys.integration.log.LogLevel.INFO));
        BlackDuckApiClient blackDuckService = blackDuckServicesFactory.getBlackDuckApiClient();
        ProjectService projectService = blackDuckServicesFactory.createProjectService();

        ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersion(getProject().getName(), getProject().getVersion().toString()).get();
        List<ProjectVersionComponentView> bomComponents = blackDuckService.getAllResponses(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.COMPONENTS_LINK_RESPONSE);
        if (bomComponents.isEmpty()) {
            throw new GradleException("No bom components were found for ${project.name} - ${version}");
        }
        bomComponents.forEach(it -> System.out.println(it.getComponentName() + " - " + it.getComponentVersionName()));
    }

}
