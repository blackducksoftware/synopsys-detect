/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.synopsys.integration.blackduck.api.generated.component.ProjectRequest;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.service.model.ProjectRequestBuilder;

public class DetectProjectRequestBuilder extends ProjectRequestBuilder {
    // private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    public DetectProjectRequestBuilder(final DetectConfiguration detectConfiguration, final DetectProject detectProject) {
        setProjectName(detectProject.getProjectName());
        setVersionName(detectProject.getProjectVersion());

        setProjectLevelAdjustments(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_LEVEL_ADJUSTMENTS));
        setPhase(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_PHASE));
        setDistribution(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_DISTRIBUTION));
        setProjectTier(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PROJECT_TIER));
        setDescription(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DESCRIPTION));
        setReleaseComments(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NOTES));

        setCloneFromReleaseUrl(detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_CLONE_URL));
        setCloneCategories(convertClonePropertyToEnum(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_CLONE_CATEGORIES)));

    }

    private List<ProjectCloneCategoriesType> convertClonePropertyToEnum(final String[] cloneCategories) {
        final List<ProjectCloneCategoriesType> categories = new ArrayList<>();
        for (final String category : cloneCategories) {
            categories.add(ProjectCloneCategoriesType.valueOf(category));
        }
        return categories;
    }

    @Override
    public ProjectRequest buildObject() {
        final ProjectRequest request = super.buildObject();

        return request;
    }
}
