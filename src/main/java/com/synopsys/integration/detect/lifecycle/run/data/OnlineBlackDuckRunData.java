package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.CodeLocationService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectMappingService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectUsersService;
import com.synopsys.integration.blackduck.service.dataservice.ReportService;
import com.synopsys.integration.blackduck.service.dataservice.TagService;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.util.NoThreadExecutorService;

public class OnlineBlackDuckRunData extends BlackDuckRunData {
    private final BlackDuckApiClient blackDuckApiClient;
    private final CodeLocationCreationService codeLocationCreationService;
    private final ProjectMappingService projectMappingService;
    private final CodeLocationService codeLocationService;
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;
    private final ProjectService projectService;
    private final ProjectBomService projectBomService;
    private final BdioUploadService bdioUploadService;
    private final ProjectUsersService projectUsersService;
    private final TagService tagService;
    private final Bdio2UploadService bdio2UploadService;
    private final BinaryScanUploadService binaryScanUploadService;
    private final ReportService reportService;
    private final ImpactAnalysisBatchRunner impactAnalysisBatchRunner;

    public static OnlineBlackDuckRunData fromFactory(PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig, BlackDuckServicesFactory blackDuckServicesFactory, long timeoutInMillis) {
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
        return new OnlineBlackDuckRunData(phoneHomeManager,
            blackDuckServerConfig,
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.createCodeLocationCreationService(),
            blackDuckServicesFactory.createProjectMappingService(),
            blackDuckServicesFactory.createCodeLocationService(),
            blackDuckServicesFactory.createProjectService(),
            blackDuckServicesFactory.createProjectBomService(),
            blackDuckServicesFactory.createBdioUploadService(),
            blackDuckServicesFactory.createProjectUsersService(),
            blackDuckServicesFactory.createTagService(),
            blackDuckServicesFactory.createBdio2UploadService(),
            blackDuckServicesFactory.createBinaryScanUploadService(),
            blackDuckServicesFactory.createReportService(timeoutInMillis),
            new ImpactAnalysisBatchRunner(blackDuckServicesFactory.getLogger(), blackDuckApiClient, new NoThreadExecutorService(), blackDuckServicesFactory.getGson()));
    }

    public OnlineBlackDuckRunData(PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig, BlackDuckApiClient blackDuckApiClient, CodeLocationCreationService codeLocationCreationService,
        ProjectMappingService projectMappingService, CodeLocationService codeLocationService, ProjectService projectService, ProjectBomService projectBomService, BdioUploadService bdioUploadService, ProjectUsersService projectUsersService,
        TagService tagService, Bdio2UploadService bdio2UploadService, BinaryScanUploadService binaryScanUploadService, ReportService reportService, ImpactAnalysisBatchRunner impactAnalysisBatchRunner) {
        super(true);
        this.blackDuckApiClient = blackDuckApiClient;
        this.codeLocationCreationService = codeLocationCreationService;
        this.projectMappingService = projectMappingService;
        this.codeLocationService = codeLocationService;
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
        this.projectService = projectService;
        this.projectBomService = projectBomService;
        this.bdioUploadService = bdioUploadService;
        this.projectUsersService = projectUsersService;
        this.tagService = tagService;
        this.bdio2UploadService = bdio2UploadService;
        this.binaryScanUploadService = binaryScanUploadService;
        this.reportService = reportService;
        this.impactAnalysisBatchRunner = impactAnalysisBatchRunner;
    }

    public Optional<PhoneHomeManager> getPhoneHomeManager() {
        return Optional.ofNullable(phoneHomeManager);
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }

    public BlackDuckApiClient getBlackDuckApiClient() {
        return blackDuckApiClient;
    }

    public CodeLocationCreationService getCodeLocationCreationService() {
        return codeLocationCreationService;
    }

    public ProjectMappingService getProjectMappingService() {
        return projectMappingService;
    }

    public CodeLocationService getCodeLocationService() {
        return codeLocationService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public ProjectBomService getProjectBomService() {
        return projectBomService;
    }

    public ProjectUsersService getProjectUsersService() {
        return projectUsersService;
    }

    public TagService getTagService() {
        return tagService;
    }

    public BdioUploadService getBdioUploadService() {
        return bdioUploadService;
    }

    public Bdio2UploadService getBdio2UploadService() {
        return bdio2UploadService;
    }

    public BinaryScanUploadService getBinaryScanUploadService() {
        return binaryScanUploadService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public ImpactAnalysisBatchRunner getImpactAnalysisBatchRunner() {
        return impactAnalysisBatchRunner;
    }
}
