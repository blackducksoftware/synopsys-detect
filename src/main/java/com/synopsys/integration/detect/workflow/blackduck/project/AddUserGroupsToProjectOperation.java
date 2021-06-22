/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.dataservice.ProjectUsersService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;

public class AddUserGroupsToProjectOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProjectUsersService projectUsersService;

    public AddUserGroupsToProjectOperation(ProjectUsersService projectUsersService) {
        this.projectUsersService = projectUsersService;
    }

    public void addUserGroupsToProject(ProjectVersionWrapper projectVersionWrapper, List<String> groupsToAddToProject) throws IntegrationException {
        for (String userGroupName : groupsToAddToProject) {
            if (StringUtils.isNotBlank(userGroupName)) {
                logger.debug(String.format("Adding user group %s to project %s", userGroupName, projectVersionWrapper.getProjectView().getName()));
                projectUsersService.addGroupToProject(projectVersionWrapper.getProjectView(), userGroupName);
            }
        }
    }
}
