package com.synopsys.integration.polaris.common.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.common.model.branch.BranchV0Resource;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resource;
import com.synopsys.integration.polaris.common.api.query.model.issue.IssueV0Attributes;
import com.synopsys.integration.polaris.common.api.query.model.issue.IssueV0Resource;
import com.synopsys.integration.polaris.common.api.query.model.issue.type.IssueTypeV0Attributes;
import com.synopsys.integration.polaris.common.api.query.model.issue.type.IssueTypeV0Resource;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.polaris.common.model.IssueResourcesSingle;

public class FullTest {
    public static void main(final String[] args) throws IntegrationException {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        final ProjectService projectService = polarisServicesFactory.createProjectService();
        final BranchService branchService = polarisServicesFactory.createBranchService();
        final IssueService issueService = polarisServicesFactory.createIssueService();

        final List<ProjectV0Resource> allProjects = projectService.getAllProjects();
        allProjects.stream().forEach(System.out::println);

        final Optional<ProjectV0Resource> project = projectService.getProjectByName("integration-common");
        System.out.println(project.get().getId());

        final Optional<BranchV0Resource> branch = branchService.getBranchForProjectByName(project.get().getId(), "17.0.1-SNAPSHOT");
        System.out.println(branch.get().getId());

        final List<IssueV0Resource> queryIssues = issueService.getIssuesForProjectAndBranch(project.get().getId(), branch.get().getId());
        queryIssues.stream().forEach(System.out::println);
        final List<String> issueKeys = queryIssues.stream().map(queryIssue -> queryIssue.getAttributes().getIssueKey()).collect(Collectors.toList());

        for (final String issueKey : issueKeys) {
            final IssueResourcesSingle issueResourcesSingle = issueService.getIssueForProjectBranchAndIssueKeyWithDefaultIncluded(project.get().getId(), branch.get().getId(), issueKey);
            final Optional<IssueTypeV0Resource> optionalIssueType = issueService.getIssueTypeFromPopulatedIssueResources(issueResourcesSingle);
            final String fullName = optionalIssueType.map(IssueTypeV0Resource::getAttributes).map(IssueTypeV0Attributes::getName).orElse("Unknown name");
            final String subTool = issueResourcesSingle.getData().map(IssueV0Resource::getAttributes).map(IssueV0Attributes::getSubTool).orElse("Unknown sub-tool");
            final String sourcePath = issueResourcesSingle.getSourcePath();
            System.out.println(subTool + ": " + fullName + " [" + sourcePath + "]");
        }
    }

}
