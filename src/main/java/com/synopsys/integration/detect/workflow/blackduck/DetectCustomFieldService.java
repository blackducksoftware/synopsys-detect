/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.api.core.BlackDuckPathMultipleResponses;
import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

public class DetectCustomFieldService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<CustomFieldOperation> determineOperations(CustomFieldDocument customFieldDocument, ProjectVersionWrapper projectVersionWrapper, BlackDuckService blackDuckService) throws DetectUserFriendlyException {
        List<CustomFieldView> projectFields = retrieveCustomFields(projectVersionWrapper.getProjectView(), blackDuckService);
        List<CustomFieldView> versionFields = retrieveCustomFields(projectVersionWrapper.getProjectVersionView(), blackDuckService);
        List<CustomFieldOperation> projectOperations = pairOperationFromViews(customFieldDocument.getProject(), projectFields, "project");
        List<CustomFieldOperation> versionOperations = pairOperationFromViews(customFieldDocument.getVersion(), versionFields, "project version");
        List<CustomFieldOperation> allOperations = new ArrayList<>();
        allOperations.addAll(projectOperations);
        allOperations.addAll(versionOperations);
        return allOperations;
    }

    private void executeCustomFieldOperations(List<CustomFieldOperation> operations, BlackDuckService blackDuckService) throws DetectUserFriendlyException {
        for (CustomFieldOperation operation : operations) {
            CustomFieldView fieldView = operation.customField;
            fieldView.setValues(operation.customFieldElement.getValue());
            try {
                blackDuckService.put(fieldView);
            } catch (IntegrationException e) {
                throw new DetectUserFriendlyException(String.format("Unable to update custom field label with name '%s'", operation.customFieldElement.getLabel()), e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }
    }

    private List<CustomFieldOperation> pairOperationFromViews(List<CustomFieldElement> elements, List<CustomFieldView> views, String targetName) throws DetectUserFriendlyException {
        List<CustomFieldOperation> operations = new ArrayList<>();
        for (CustomFieldElement element : elements) {
            Optional<CustomFieldView> fieldView = views.stream()
                                                      .filter(view -> view.getLabel().equals(element.getLabel()))
                                                      .findFirst();

            if (!fieldView.isPresent()) {
                throw new DetectUserFriendlyException(String.format("Unable to find custom field view with label '%s' on the %s. Ensure it exists.", element.getLabel(), targetName), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }

            operations.add(new CustomFieldOperation(fieldView.get(), element));
        }
        return operations;
    }

    private List<CustomFieldView> retrieveCustomFields(BlackDuckView view, BlackDuckService blackDuckService) {
        final Optional<String> customFieldLink = view.getFirstLink("custom-fields");
        if (!customFieldLink.isPresent()) {
            return Collections.emptyList();
        }
        try {
            return blackDuckService.getAllResponses(new BlackDuckPathMultipleResponses<>(new BlackDuckPath(customFieldLink.get()), CustomFieldView.class));
        } catch (IntegrationException e) {
            return Collections.emptyList();
        }
    }

    public void updateCustomFields(ProjectVersionWrapper projectVersionWrapper, CustomFieldDocument customFieldDocument, BlackDuckService blackDuckService) throws DetectUserFriendlyException {
        List<CustomFieldOperation> customFieldOperations = determineOperations(customFieldDocument, projectVersionWrapper, blackDuckService);
        executeCustomFieldOperations(customFieldOperations, blackDuckService);
    }
}
