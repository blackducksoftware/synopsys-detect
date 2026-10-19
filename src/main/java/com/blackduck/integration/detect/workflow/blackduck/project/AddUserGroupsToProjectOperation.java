package com.blackduck.integration.detect.workflow.blackduck.project;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.api.manual.view.ProjectView;
import com.blackduck.integration.blackduck.service.dataservice.ProjectUsersService;
import com.blackduck.integration.blackduck.service.model.ProjectVersionWrapper;
import com.blackduck.integration.exception.IntegrationException;

public class AddUserGroupsToProjectOperation {
    private static final boolean BENCHMARK_ENABLED = true; // BDSCA-2360: set true to log slow vs fast timing

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectUsersService projectUsersService;

    public AddUserGroupsToProjectOperation(ProjectUsersService projectUsersService) {
        this.projectUsersService = projectUsersService;
    }

    public void addUserGroupsToProject(ProjectVersionWrapper projectVersionWrapper, List<String> groupsToAddToProject) throws IntegrationException {
        if (BENCHMARK_ENABLED) {
            benchmarkAndAssign(projectVersionWrapper.getProjectView(), groupsToAddToProject);
            return;
        }

        for (String userGroupName : groupsToAddToProject) {
            if (StringUtils.isNotBlank(userGroupName)) {
                logger.debug(String.format("Adding user group %s to project %s", userGroupName, projectVersionWrapper.getProjectView().getName()));
                projectUsersService.addGroupToProject(projectVersionWrapper.getProjectView(), userGroupName);
            }
        }
    }

    private void benchmarkAndAssign(ProjectView projectView, List<String> groupsToAddToProject) throws IntegrationException {
        List<String> nonBlank = groupsToAddToProject.stream()
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());

        int half = nonBlank.size() / 2;
        List<String> slowGroups = nonBlank.subList(0, half);
        List<String> fastGroups = nonBlank.subList(half, nonBlank.size());

        long slowStart = System.currentTimeMillis();
        for (String name : slowGroups) {
            projectUsersService.addGroupToProjectSlow(projectView, name);
        }
        long slowMs = System.currentTimeMillis() - slowStart;

        long fastStart = System.currentTimeMillis();
        for (String name : fastGroups) {
            projectUsersService.addGroupToProject(projectView, name);
        }
        long fastMs = System.currentTimeMillis() - fastStart;

        logger.info(String.format(
            "[BDSCA-2360] slow=%d ms (%d groups, %.0f ms/group)  fast=%d ms (%d groups, %.0f ms/group)  diff=%d ms",
            slowMs, slowGroups.size(), slowGroups.isEmpty() ? 0.0 : (double) slowMs / slowGroups.size(),
            fastMs, fastGroups.size(), fastGroups.isEmpty() ? 0.0 : (double) fastMs / fastGroups.size(),
            slowMs - fastMs));
    }
}
