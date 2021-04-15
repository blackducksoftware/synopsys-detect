package com.synopsys.integration.detect.workflow.blackduck.report.service;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.manual.temporary.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.integration.BlackDuckIntegrationTest;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class RiskReportServiceTestIT extends BlackDuckIntegrationTest {
    public static final String PROJECT_NAME = "detect risk report test";
    public static final String PROJECT_VERSION_NAME = "1.0.0";

    @BeforeAll
    public static void createProjectFirst() throws IntegrationException {
        String testPhase = ProjectVersionPhaseType.DEVELOPMENT.name();
        String testDistribution = ProjectVersionDistributionType.OPENSOURCE.name();

        ProjectSyncModel projectSyncModel = new ProjectSyncModel(PROJECT_NAME, PROJECT_VERSION_NAME);
        projectSyncModel.setPhase(ProjectVersionPhaseType.valueOf(testPhase));
        projectSyncModel.setDistribution(ProjectVersionDistributionType.valueOf(testDistribution));

        projectService.syncProjectAndVersion(projectSyncModel);
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void createReportPdfFileTest(@TempDirectory.TempDir Path folderForReport) throws IntegrationException {
        Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(PROJECT_NAME, PROJECT_VERSION_NAME);
        ProjectView projectView = projectVersionWrapper.get().getProjectView();
        ProjectVersionView projectVersionView = projectVersionWrapper.get().getProjectVersionView();

        File folder = folderForReport.toFile();
        File pdfFile = reportService.createReportPdfFile(folder, projectView, projectVersionView);
        Assertions.assertNotNull(pdfFile);
        Assertions.assertTrue(pdfFile.exists());
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void createNoticesReportFileTest(@TempDirectory.TempDir Path folderForReport) throws IntegrationException, InterruptedException {
        Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(PROJECT_NAME, PROJECT_VERSION_NAME);
        ProjectView projectView = projectVersionWrapper.get().getProjectView();
        ProjectVersionView projectVersionView = projectVersionWrapper.get().getProjectVersionView();

        File noticeReportFile = reportService.createNoticesReportFile(folderForReport.toFile(), projectView, projectVersionView);
        Assertions.assertNotNull(noticeReportFile);
        Assertions.assertTrue(noticeReportFile.exists());
    }

    @Test
    @Disabled
    public void createReportFilesManually() throws IntegrationException, InterruptedException {
        // fill these values in with your particulars
        final String projectName = "docker.pkg.github.com_maja-nord_java-eap-app_java-eap-app";
        final String projectVersionName = "latest";
        final String localPathForPdfReport = "/Users/ekerwin/Documents/working/riskreport_pdf";
        final String localPathForNoticesReport = "/Users/ekerwin/Documents/working/notices";

        Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        ProjectView projectView = projectVersionWrapper.get().getProjectView();
        ProjectVersionView projectVersionView = projectVersionWrapper.get().getProjectVersionView();

        File pdfReportFolder = new File(localPathForPdfReport);
        File noticesReportFolder = new File(localPathForNoticesReport);

        pdfReportFolder.mkdirs();
        noticesReportFolder.mkdirs();

        reportService.createReportPdfFile(pdfReportFolder, projectView, projectVersionView);
        reportService.createNoticesReportFile(noticesReportFolder, projectView, projectVersionView);
    }

}
