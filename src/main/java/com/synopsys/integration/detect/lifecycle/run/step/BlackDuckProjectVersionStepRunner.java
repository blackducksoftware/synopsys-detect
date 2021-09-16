/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.FindCloneOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ParentProjectMapOptions;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckProjectVersionStepRunner {
    private OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BlackDuckProjectVersionStepRunner(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    ProjectVersionWrapper runAll(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        CloneFindResult cloneFindResult = findClone(projectNameVersion.getName(), blackDuckRunData);
        ProjectVersionWrapper projectVersion = operationFactory.syncProjectVersion(projectNameVersion, cloneFindResult, blackDuckRunData);

        ParentProjectMapOptions mapOptions = operationFactory.calculateParentProjectMapOptions();
        if (StringUtils.isNotBlank(mapOptions.getParentProjectName()) || StringUtils.isNotBlank(mapOptions.getParentProjectVersionName())) {
            operationFactory.mapToParentProject(mapOptions.getParentProjectName(), mapOptions.getParentProjectVersionName(), projectVersion, blackDuckRunData);
        }

        String applicationId = operationFactory.calculateApplicationId();
        if (StringUtils.isBlank(applicationId)) {
            logger.debug("No 'Application ID' to set.");
        } else {
            operationFactory.setApplicationId(applicationId, projectVersion, blackDuckRunData);
        }

        CustomFieldDocument customFieldDocument = operationFactory.calculateCustomFields();
        if (customFieldDocument == null || (customFieldDocument.getProject().size() == 0 && customFieldDocument.getVersion().size() == 0)) {
            logger.debug("No custom fields to set.");
        } else {
            operationFactory.updateCustomFields(customFieldDocument, projectVersion, blackDuckRunData);
        }

        List<String> userGroups = operationFactory.calculateUserGroups();
        if (userGroups == null) {
            logger.debug("No user groups to set.");
        } else {
            operationFactory.addUserGroups(userGroups, projectVersion, blackDuckRunData);
        }

        List<String> tags = operationFactory.calculateTags();
        if (tags == null) {
            logger.debug("No tags to set.");
        } else {
            operationFactory.addTags(tags, projectVersion, blackDuckRunData);
        }

        if (operationFactory.calculateShouldUnmap()) {
            logger.debug("Unmapping code locations.");
            operationFactory.unmapCodeLocations(projectVersion, blackDuckRunData);
        } else {
            logger.debug("Will not unmap code locations: Project view was not present, or should not unmap code locations.");
        }

        return projectVersion;
    }

    private CloneFindResult findClone(String projectName, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        FindCloneOptions cloneOptions = operationFactory.calculateCloneOptions();
        if (cloneOptions.getCloneLatestProjectVersion()) {
            logger.debug("Cloning the most recent project version.");
            return operationFactory.findLatestProjectVersionCloneUrl(blackDuckRunData, projectName);
        } else if (StringUtils.isNotBlank(cloneOptions.getCloneVersionName())) {
            return operationFactory.findNamedCloneUrl(blackDuckRunData, projectName, cloneOptions.getCloneVersionName());
        } else {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return CloneFindResult.empty();
        }
    }
}
