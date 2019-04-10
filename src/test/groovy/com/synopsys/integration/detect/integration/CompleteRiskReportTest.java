package com.synopsys.integration.detect.integration;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class CompleteRiskReportTest {
    @Test
    public void testRiskReportIsPopulated() throws Exception {
        IntLogger logger = new BufferedIntLogger();
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckServerConfigBuilder.build().createBlackDuckServicesFactory(logger);

        BlackDuckService blackDuckService = blackDuckServicesFactory.createBlackDuckService();
        ProjectService projectService = blackDuckServicesFactory.createProjectService();
        ReportService reportService = blackDuckServicesFactory.createReportService(120 * 1000);
        Path tempReportDirectoryPath = Files.createTempDirectory("junit_report");
        File tempReportDirectory = tempReportDirectoryPath.toFile();

        String projectName = "synopsys-detect-junit";
        String projectVersionName = "risk-report";
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        if (optionalProjectVersionWrapper.isPresent()) {
            blackDuckService.delete(optionalProjectVersionWrapper.get().getProjectView());
        }

        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectName, projectVersionName);
        projectService.syncProjectAndVersion(projectSyncModel);
        optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        assertTrue(optionalProjectVersionWrapper.isPresent());

        List<File> pdfFiles = getPdfFiles(tempReportDirectory);
        assertEquals(0, pdfFiles.size());

        reportService.createReportPdfFile(tempReportDirectory, optionalProjectVersionWrapper.get().getProjectView(), optionalProjectVersionWrapper.get().getProjectVersionView());
        pdfFiles = getPdfFiles(tempReportDirectory);
        assertEquals(1, pdfFiles.size());

        long initialFileLength = pdfFiles.get(0).length();
        assertTrue(initialFileLength > 0);
        FileUtils.deleteQuietly(pdfFiles.get(0));

        pdfFiles = getPdfFiles(tempReportDirectory);
        assertEquals(0, pdfFiles.size());
        String[] detectArgs = new String[]{
                "--detect.project.name=" + projectName,
                "--detect.project.version.name=" + projectVersionName,
                "--detect.risk.report.pdf=true",
                "--detect.risk.report.pdf.path=" + tempReportDirectory.toString()
        };
        Application.main(detectArgs);

        pdfFiles = getPdfFiles(tempReportDirectory);
        assertEquals(1, pdfFiles.size());
        long postLength = pdfFiles.get(0).length();
        assertTrue(postLength > initialFileLength);
    }

    private List<File> getPdfFiles(File directory) {
        return Arrays.stream(directory.listFiles())
                .filter(file -> file.getName().endsWith(".pdf"))
                .collect(Collectors.toList());
    }

}
