package com.synopsys.integration.detect.workflow.blackduck.report;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.manual.temporary.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class RiskReportServiceTestIT {
    public static final String PROJECT_NAME = "detect risk report test";
    public static final String PROJECT_VERSION_NAME = "1.0.0";

    @BeforeAll
    public static void createProjectFirst() throws IntegrationException {

        String testPhase = ProjectVersionPhaseType.DEVELOPMENT.name();
        String testDistribution = ProjectVersionDistributionType.OPENSOURCE.name();

        ProjectSyncModel projectSyncModel = new ProjectSyncModel(PROJECT_NAME, PROJECT_VERSION_NAME);
        projectSyncModel.setPhase(ProjectVersionPhaseType.valueOf(testPhase));
        projectSyncModel.setDistribution(ProjectVersionDistributionType.valueOf(testDistribution));

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        blackDuckTestConnection.createProjectService().syncProjectAndVersion(projectSyncModel);
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void createReportPdfFileTest(@TempDirectory.TempDir Path folderForReport) throws IntegrationException {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        ReportService reportService = blackDuckTestConnection.createReportService();

        ProjectVersionWrapper projectVersionWrapper = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, PROJECT_VERSION_NAME).retrieveProjectVersionWrapper();

        File folder = folderForReport.toFile();
        File pdfFile = reportService.createReportPdfFile(folder, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView());
        Assertions.assertNotNull(pdfFile);
        Assertions.assertTrue(pdfFile.exists());
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void createNoticesReportFileTest(@TempDirectory.TempDir Path folderForReport) throws IntegrationException, InterruptedException {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        ReportService reportService = blackDuckTestConnection.createReportService();

        ProjectVersionWrapper projectVersionWrapper = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, PROJECT_VERSION_NAME).retrieveProjectVersionWrapper();

        File noticeReportFile = reportService.createNoticesReportFile(
            folderForReport.toFile(),
            projectVersionWrapper.getProjectView(),
            projectVersionWrapper.getProjectVersionView()
        );
        Assertions.assertNotNull(noticeReportFile);
        Assertions.assertTrue(noticeReportFile.exists());
    }

    @Test
    @Disabled
    public void createReportFilesManually() throws IntegrationException, InterruptedException {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        ReportService reportService = blackDuckTestConnection.createReportService();

        // fill these values in with your particulars
        final String projectName = "docker.pkg.github.com_maja-nord_java-eap-app_java-eap-app";
        final String projectVersionName = "latest";
        final String localPathForPdfReport = "/Users/ekerwin/Documents/working/riskreport_pdf";
        final String localPathForNoticesReport = "/Users/ekerwin/Documents/working/notices";

        ProjectVersionWrapper projectVersionWrapper = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, PROJECT_VERSION_NAME).retrieveProjectVersionWrapper();

        File pdfReportFolder = new File(localPathForPdfReport);
        File noticesReportFolder = new File(localPathForNoticesReport);

        pdfReportFolder.mkdirs();
        noticesReportFolder.mkdirs();

        reportService.createReportPdfFile(pdfReportFolder, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView());
        reportService.createNoticesReportFile(noticesReportFolder, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView());
    }

}
