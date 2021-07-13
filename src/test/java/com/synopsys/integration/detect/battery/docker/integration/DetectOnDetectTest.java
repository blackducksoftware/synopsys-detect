package com.synopsys.integration.detect.battery.docker.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestBuilder;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class DetectOnDetectTest {
    @Test
    void detectOnDetect() throws IOException, InterruptedException, IntegrationException {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-on-detect", "detect-7.1.0:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("detect-on-detect-docker", "happy-path");
        blackduckAssertions.emptyOnBlackDuck();

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
        commandBuilder.connectToBlackDuck(blackDuckTestConnection);
        commandBuilder.projectNameVersion(blackduckAssertions);
        commandBuilder.waitForResults();

        DockerAssertions dockerAssertions = test.run(commandBuilder);

        dockerAssertions.bdioFiles(6); //7 code locations, 6 bdio, 1 signature scanner

        blackduckAssertions.hasCodeLocations("src/detect-on-detect-docker/happy-path scan",
            "detect-on-detect-docker/happy-path/detectable/com.synopsys.integration/detectable/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/com.synopsys.integration/synopsys-detect/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/common/com.synopsys.integration/common/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/common-test/com.synopsys.integration/common-test/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/configuration/com.synopsys.integration/configuration/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/detector/com.synopsys.integration/detector/7.1.1-SNAPSHOT gradle/bom");

        blackduckAssertions.hasComponents("jackson-core");
    }

    private static final long HALF_MILLION_BYTES = 500_000;

    @Test
    @ExtendWith(TempDirectory.class)
    public void testDryRunScanWithSnippetMatching(@TempDirectory.TempDir final Path tempOutputDirectory) throws Exception {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-on-detect-dryrun", "detect-7.1.0:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

        final String projectName = "synopsys-detect-junit";
        final String projectVersionName = "dryrun-scan";
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckAssertions blackDuckAssertions = blackDuckTestConnection.projectVersionAssertions(projectName, projectVersionName);

        blackDuckAssertions.emptyOnBlackDuck();

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
        commandBuilder.projectNameVersion(blackDuckAssertions.getProjectNameVersion());
        commandBuilder.connectToBlackDuck(blackDuckTestConnection);
        commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING, "SNIPPET_MATCHING");
        commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, "true");

        DockerAssertions dockerAssertions = test.run(commandBuilder);
        assertDirectoryStructureForOfflineScan(dockerAssertions.getOutputDirectory().toPath());
    }

    @Test
    //Simply verify a risk report is generated at the expected location.
    public void riskReportResultProduced() throws Exception {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-on-detect-riskreport-default", "detect-7.1.0:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckAssertions blackDuckAssertions = blackDuckTestConnection.projectVersionAssertions("synopsys-detect-junit", "risk-report-default");
        blackDuckAssertions.emptyOnBlackDuck();

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
        commandBuilder.connectToBlackDuck(blackDuckTestConnection);
        commandBuilder.projectNameVersion(blackDuckAssertions.getProjectNameVersion());
        commandBuilder.property(DetectProperties.DETECT_RISK_REPORT_PDF, "true");
        commandBuilder.property(DetectProperties.DETECT_TIMEOUT, "1200");
        commandBuilder.tools(DetectTool.DETECTOR);

        DockerAssertions dockerAssertions = test.run(commandBuilder);
        dockerAssertions.resultProducedAtLocation("/opt/project/src/synopsys_detect_junit_risk_report_default_BlackDuck_RiskReport.pdf");
    }

    @Test
    //Tests that a new project has an empty report, run detect to fill it, tests the report is filled, in a custom location
    public void riskReportPopulatedAtCustomPath() throws Exception {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-on-detect-riskreport-custom", "detect-7.1.0:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        ReportService reportService = blackDuckTestConnection.createReportService();

        BlackDuckAssertions blackDuckAssertions = blackDuckTestConnection.projectVersionAssertions("synopsys-detect-junit", "risk-report-custom");
        ProjectVersionWrapper projectVersionWrapper = blackDuckAssertions.emptyOnBlackDuck();

        String reportDirectoryImagePath = "/opt/report";
        File reportDirectory = test.directories().createResultDirectory("report");
        test.directories().withBinding(reportDirectory, reportDirectoryImagePath);

        long initialFileLength = assertEmptyRiskReport(reportDirectory, projectVersionWrapper, reportService);

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
        commandBuilder.connectToBlackDuck(blackDuckTestConnection);
        commandBuilder.projectNameVersion(blackDuckAssertions.getProjectNameVersion());
        commandBuilder.property(DetectProperties.DETECT_RISK_REPORT_PDF, "true");
        commandBuilder.property(DetectProperties.DETECT_TIMEOUT, "1200");
        commandBuilder.property(DetectProperties.DETECT_RISK_REPORT_PDF_PATH, reportDirectoryImagePath);
        commandBuilder.tools(DetectTool.DETECTOR);

        test.run(commandBuilder);

        List<File> pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(1, pdfFiles.size());
        long postLength = pdfFiles.get(0).length();
        assertTrue(postLength > initialFileLength);
    }

    private long assertEmptyRiskReport(File reportDirectory, ProjectVersionWrapper projectVersionWrapper, ReportService reportService) throws IntegrationException {
        List<File> pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(0, pdfFiles.size());
        File riskReportPdf = reportService.createReportPdfFile(reportDirectory, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView());
        pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(1, pdfFiles.size());
        long initialFileLength = pdfFiles.get(0).length();
        assertTrue(initialFileLength > 0);
        FileUtils.deleteQuietly(pdfFiles.get(0));
        pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(0, pdfFiles.size());

        return initialFileLength;
    }

    private List<File> getPdfFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.stream(files)
                       .filter(file -> file.getName().endsWith(".pdf"))
                       .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private void assertDirectoryStructureForOfflineScan(Path tempOutputDirectory) {
        final Path runsPath = tempOutputDirectory.resolve("runs");
        assertTrue(runsPath.toFile().exists());
        assertTrue(runsPath.toFile().isDirectory());

        final File[] runDirectories = runsPath.toFile().listFiles();

        assertNotNull(runDirectories);
        assertEquals(1, runDirectories.length);

        final File runDirectory = runDirectories[0];
        assertTrue(runDirectory.exists());
        assertTrue(runDirectory.isDirectory());

        final File scanDirectory = new File(runDirectory, "scan");
        assertTrue(scanDirectory.exists());
        assertTrue(scanDirectory.isDirectory());

        final File blackDuckScanOutput = new File(scanDirectory, "BlackDuckScanOutput");
        assertTrue(blackDuckScanOutput.exists());
        assertTrue(blackDuckScanOutput.isDirectory());

        final File[] outputDirectories = blackDuckScanOutput.listFiles();
        assertNotNull(outputDirectories);
        assertEquals(1, outputDirectories.length);

        final File outputDirectory = outputDirectories[0];
        assertTrue(outputDirectory.exists());
        assertTrue(outputDirectory.isDirectory());

        final File dataDirectory = new File(outputDirectory, "data");
        assertTrue(dataDirectory.exists());
        assertTrue(dataDirectory.isDirectory());

        final File[] dataFiles = dataDirectory.listFiles();
        assertNotNull(dataFiles);
        assertEquals(1, dataFiles.length);
        assertTrue(dataFiles[0].length() > HALF_MILLION_BYTES);
    }

}
