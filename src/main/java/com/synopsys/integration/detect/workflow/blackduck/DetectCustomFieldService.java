/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.core.response.LinkMultipleResponses;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

public class DetectCustomFieldService {
    public static final LinkMultipleResponses<CustomFieldView> CUSTOM_FIELDS_LINK = new LinkMultipleResponses<>("custom-fields", CustomFieldView.class);
    public static final LinkMultipleResponses<CustomFieldOptionView> CUSTOM_FIELDS_OPTION_LIST_LINK = new LinkMultipleResponses<>("custom-field-option-list", CustomFieldOptionView.class);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<CustomFieldOperation> determineOperations(final CustomFieldDocument customFieldDocument, final ProjectVersionWrapper projectVersionWrapper, final BlackDuckApiClient blackDuckService) throws DetectUserFriendlyException {
        final List<CustomFieldView> projectFields = retrieveCustomFields(projectVersionWrapper.getProjectView(), blackDuckService);
        final List<CustomFieldView> versionFields = retrieveCustomFields(projectVersionWrapper.getProjectVersionView(), blackDuckService);
        final List<CustomFieldOperation> projectOperations = pairOperationFromViews(customFieldDocument.getProject(), projectFields, "project", blackDuckService);
        final List<CustomFieldOperation> versionOperations = pairOperationFromViews(customFieldDocument.getVersion(), versionFields, "project version", blackDuckService);
        final List<CustomFieldOperation> allOperations = new ArrayList<>();
        allOperations.addAll(projectOperations);
        allOperations.addAll(versionOperations);
        return allOperations;
    }

    //radio, multiselect, and dropdown
    private void executeCustomFieldOperations(final List<CustomFieldOperation> operations, final BlackDuckApiClient blackDuckService) throws DetectUserFriendlyException {
        for (final CustomFieldOperation operation : operations) {
            final CustomFieldView fieldView = operation.customField;
            fieldView.setValues(operation.values);
            try {
                blackDuckService.put(fieldView);
            } catch (final IntegrationException e) {
                throw new DetectUserFriendlyException(String.format("Unable to update custom field label with name '%s'", operation.customField.getLabel()), e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }
    }

    private List<CustomFieldOperation> pairOperationFromViews(final List<CustomFieldElement> elements, final List<CustomFieldView> views, final String targetName, final BlackDuckApiClient blackDuckService)
        throws DetectUserFriendlyException {
        final List<CustomFieldOperation> operations = new ArrayList<>();
        for (final CustomFieldElement element : elements) {
            final Optional<CustomFieldView> fieldView = views.stream()
                                                            .filter(view -> view.getLabel().equals(element.getLabel()))
                                                            .findFirst();

            if (!fieldView.isPresent()) {
                throw new DetectUserFriendlyException(String.format("Unable to find custom field view with label '%s' on the %s. Ensure it exists.", element.getLabel(), targetName), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }

            List<String> values = new ArrayList<>();
            final List<CustomFieldOptionView> options = retrieveCustomFieldOptions(fieldView.get(), blackDuckService);
            if (options.isEmpty()) {
                logger.debug("Did not find any associated options for this field, will use raw values.");
                values = element.getValue();
            } else {
                logger.debug("Found one or more options for this field. Will attempt to map given values to fields..");
                for (final String value : element.getValue()) {
                    final Optional<CustomFieldOptionView> option = options.stream()
                                                                       .filter(it -> it.getLabel().equals(value))
                                                                       .findFirst();
                    if (option.isPresent()) {
                        values.add(option.get().getHref().string());
                    } else {
                        throw new DetectUserFriendlyException(String.format("Unable to update custom field '%s', unable to find option for value '%s'", element.getLabel(), value),
                            ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
                    }
                }
            }

            operations.add(new CustomFieldOperation(fieldView.get(), values));
        }
        return operations;
    }

    //TODO ejk There is no way this is a reasonable implementation. :)
    private List<CustomFieldView> retrieveCustomFields(final BlackDuckView view, final BlackDuckApiClient blackDuckService) {
        try {
            return blackDuckService.getAllResponses(view.metaMultipleResponses(CUSTOM_FIELDS_LINK));
        } catch (final IntegrationException | NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    private List<CustomFieldOptionView> retrieveCustomFieldOptions(final BlackDuckView view, final BlackDuckApiClient blackDuckService) {
        try {
            return blackDuckService.getAllResponses(view.metaMultipleResponses(CUSTOM_FIELDS_OPTION_LIST_LINK));
        } catch (final IntegrationException | NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    public void updateCustomFields(final ProjectVersionWrapper projectVersionWrapper, final CustomFieldDocument customFieldDocument, final BlackDuckApiClient blackDuckService) throws DetectUserFriendlyException {
        final List<CustomFieldOperation> customFieldOperations = determineOperations(customFieldDocument, projectVersionWrapper, blackDuckService);
        executeCustomFieldOperations(customFieldOperations, blackDuckService);
    }

}
