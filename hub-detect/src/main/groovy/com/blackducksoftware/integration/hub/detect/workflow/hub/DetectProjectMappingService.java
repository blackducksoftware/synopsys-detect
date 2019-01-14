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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.LinkSingleResponse;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class DetectProjectMappingService {
    public static final LinkSingleResponse<DetectProjectMappingView> PROJECT_MAPPINGS_LINK_RESPONSE = new LinkSingleResponse<>("project-mappings", DetectProjectMappingView.class);

    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final BlackDuckService blackDuckService;

    public DetectProjectMappingService(final BlackDuckService blackDuckService) {
        this.blackDuckService = blackDuckService;
    }

    /**
     * Sets the applicationId for a project
     * @return The url of the project mapping
     * @throws IntegrationException
     */
    public String setApplicationId(final ProjectView projectView, final String applicationId) throws IntegrationException {
        final Optional<String> projectMappingsLink = projectView.getFirstLink(PROJECT_MAPPINGS_LINK_RESPONSE.getLink());

        if (projectMappingsLink.isPresent()) {
            // TODO: Currently there exists only one project-mapping which is the application id. Eventually there will be many so we need to filter
            final Optional<DetectProjectMappingView> existingProjectMappingView = getProjectMappings(projectView).stream()
                                                                                      .findFirst();
            if (existingProjectMappingView.isPresent()) {
                deleteProjectMapping(existingProjectMappingView.get());
            }

            final DetectProjectMappingView detectProjectMappingView = new DetectProjectMappingView();
            detectProjectMappingView.setApplicationId(applicationId);

            return blackDuckService.post(projectMappingsLink.get(), detectProjectMappingView);
        } else {
            throw new IntegrationException("project-mappings link not found in projectView");
        }
    }

    public List<DetectProjectMappingView> getProjectMappings(final ProjectView projectView) throws IntegrationException {
        final Optional<String> projectMappingsLink = projectView.getFirstLink(PROJECT_MAPPINGS_LINK_RESPONSE.getLink());
        if (projectMappingsLink.isPresent()) {
            return blackDuckService.getAllResponses(projectMappingsLink.get(), DetectProjectMappingView.class);
        } else {
            throw new IntegrationException("project-mappings link not found in projectView");
        }
    }

    public void deleteProjectMapping(final DetectProjectMappingView detectProjectMappingView) throws IntegrationException {
        final Optional<String> href = detectProjectMappingView.getHref();
        if (href.isPresent()) {
            blackDuckService.delete(href.get());
        } else {
            logger.error("Cannot delete project mapping because no href was found");
        }
    }
}
