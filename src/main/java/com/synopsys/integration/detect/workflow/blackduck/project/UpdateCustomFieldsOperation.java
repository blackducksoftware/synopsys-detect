package com.synopsys.integration.detect.workflow.blackduck.project;

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
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldElement;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldOptionView;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldView;
import com.synopsys.integration.exception.IntegrationException;

public class UpdateCustomFieldsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final LinkMultipleResponses<CustomFieldView> CUSTOM_FIELDS_LINK = new LinkMultipleResponses<>("custom-fields", CustomFieldView.class);
    public static final LinkMultipleResponses<CustomFieldOptionView> CUSTOM_FIELDS_OPTION_LIST_LINK = new LinkMultipleResponses<>(
        "custom-field-option-list",
        CustomFieldOptionView.class
    );
    private final BlackDuckApiClient blackDuckService;

    public UpdateCustomFieldsOperation(BlackDuckApiClient blackDuckService) {
        this.blackDuckService = blackDuckService;
    }

    public void updateCustomFields(ProjectVersionWrapper projectVersionWrapper, CustomFieldDocument customFieldDocument) throws DetectUserFriendlyException {
        logger.debug("Will update the following custom fields and values.");
        for (CustomFieldElement element : customFieldDocument.getProject()) {
            logger.debug(String.format("Project field '%s' will be set to '%s'.", element.getLabel(), String.join(",", element.getValue())));
        }
        for (CustomFieldElement element : customFieldDocument.getVersion()) {
            logger.debug(String.format("Version field '%s' will be set to '%s'.", element.getLabel(), String.join(",", element.getValue())));
        }

        List<CustomFieldOperation> customFieldOperations = determineOperations(customFieldDocument, projectVersionWrapper, blackDuckService);
        executeCustomFieldOperations(customFieldOperations, blackDuckService);

        logger.info("Successfully updated (" + (customFieldDocument.getVersion().size() + customFieldDocument.getProject().size()) + ") custom fields.");
    }

    private List<CustomFieldOperation> determineOperations(
        CustomFieldDocument customFieldDocument,
        ProjectVersionWrapper projectVersionWrapper,
        BlackDuckApiClient blackDuckService
    ) throws DetectUserFriendlyException {
        List<CustomFieldView> projectFields = retrieveCustomFields(projectVersionWrapper.getProjectView(), blackDuckService);
        List<CustomFieldView> versionFields = retrieveCustomFields(projectVersionWrapper.getProjectVersionView(), blackDuckService);
        List<CustomFieldOperation> projectOperations = pairOperationFromViews(customFieldDocument.getProject(), projectFields, "project", blackDuckService);
        List<CustomFieldOperation> versionOperations = pairOperationFromViews(customFieldDocument.getVersion(), versionFields, "project version", blackDuckService);
        List<CustomFieldOperation> allOperations = new ArrayList<>();
        allOperations.addAll(projectOperations);
        allOperations.addAll(versionOperations);
        return allOperations;
    }

    //radio, multiselect, and dropdown
    private void executeCustomFieldOperations(List<CustomFieldOperation> operations, BlackDuckApiClient blackDuckService) throws DetectUserFriendlyException {
        for (CustomFieldOperation operation : operations) {
            CustomFieldView fieldView = operation.customField;
            fieldView.setValues(operation.values);
            try {
                blackDuckService.put(fieldView);
            } catch (IntegrationException e) {
                throw new DetectUserFriendlyException(
                    String.format("Unable to update custom field label with name '%s'", operation.customField.getLabel()),
                    e,
                    ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR
                );
            }
        }
    }

    private List<CustomFieldOperation> pairOperationFromViews(
        List<CustomFieldElement> elements,
        List<CustomFieldView> views,
        String targetName,
        BlackDuckApiClient blackDuckService
    )
        throws DetectUserFriendlyException {
        List<CustomFieldOperation> operations = new ArrayList<>();
        for (CustomFieldElement element : elements) {
            Optional<CustomFieldView> fieldView = views.stream()
                .filter(view -> view.getLabel().equals(element.getLabel()))
                .findFirst();

            if (!fieldView.isPresent()) {
                throw new DetectUserFriendlyException(String.format(
                    "Unable to find custom field view with label '%s' on the %s. Ensure it exists.",
                    element.getLabel(),
                    targetName
                ), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }

            List<String> values = new ArrayList<>();
            List<CustomFieldOptionView> options = retrieveCustomFieldOptions(fieldView.get(), blackDuckService);
            if (options.isEmpty()) {
                logger.debug("Did not find any associated options for this field, will use raw values.");
                values = element.getValue();
            } else {
                logger.debug("Found one or more options for this field. Will attempt to map given values to fields..");
                for (String value : element.getValue()) {
                    Optional<CustomFieldOptionView> option = options.stream()
                        .filter(it -> it.getLabel().equals(value))
                        .findFirst();
                    if (option.isPresent()) {
                        values.add(option.get().getHref().string());
                    } else {
                        throw new DetectUserFriendlyException(
                            String.format("Unable to update custom field '%s', unable to find option for value '%s'", element.getLabel(), value),
                            ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR
                        );
                    }
                }
            }

            operations.add(new CustomFieldOperation(fieldView.get(), values));
        }
        return operations;
    }

    //TODO ejk There is no way this is a reasonable implementation. :)
    private List<CustomFieldView> retrieveCustomFields(BlackDuckView view, BlackDuckApiClient blackDuckService) {
        try {
            return blackDuckService.getAllResponses(view.metaMultipleResponses(CUSTOM_FIELDS_LINK));
        } catch (IntegrationException | NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    private List<CustomFieldOptionView> retrieveCustomFieldOptions(BlackDuckView view, BlackDuckApiClient blackDuckService) {
        try {
            return blackDuckService.getAllResponses(view.metaMultipleResponses(CUSTOM_FIELDS_OPTION_LIST_LINK));
        } catch (IntegrationException | NoSuchElementException e) {
            return Collections.emptyList();
        }
    }
}
