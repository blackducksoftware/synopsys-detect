package com.synopsys.integration.detect.integration;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class SignatureScanTest {
    private static final long ONE_MILLION_BYTES = 1_000_000;

    private static IntLogger logger;
    private static BlackDuckServicesFactory blackDuckServicesFactory;
    private static boolean previousDoNotExit;

    @BeforeAll
    public static void setup() {
        logger = new BufferedIntLogger();
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());
        blackDuckServicesFactory = blackDuckServerConfigBuilder.build().createBlackDuckServicesFactory(logger);

        previousDoNotExit = Application.SHOULD_EXIT;
        Application.SHOULD_EXIT = false;
    }

    @AfterAll
    public static void cleanup() {
        Application.SHOULD_EXIT = previousDoNotExit;
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void testOfflineScanWithSnippetMatching(@TempDirectory.TempDir Path tempOutputDirectory) throws Exception {
        BlackDuckService blackDuckService = blackDuckServicesFactory.createBlackDuckService();
        ProjectService projectService = blackDuckServicesFactory.createProjectService();

        String projectName = "synopsys-detect-junit";
        String projectVersionName = "offline-scan";
        Optional<ProjectVersionWrapper> optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        if (optionalProjectVersionWrapper.isPresent()) {
            blackDuckService.delete(optionalProjectVersionWrapper.get().getProjectView());
        }

        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectName, projectVersionName);
        projectService.syncProjectAndVersion(projectSyncModel);
        optionalProjectVersionWrapper = projectService.getProjectVersion(projectName, projectVersionName);
        assertTrue(optionalProjectVersionWrapper.isPresent());

        String[] detectArgs = new String[] {
                "--detect.output.path=" + tempOutputDirectory.toString(),
                "--detect.project.name=" + projectName,
                "--detect.project.version.name=" + projectVersionName,
                "--detect.blackduck.signature.scanner.snippet.matching=SNIPPET_MATCHING",
                "--detect.blackduck.signature.scanner.dry.run=true"
        };
        Application.main(detectArgs);

        Path runsPath = tempOutputDirectory.resolve("runs");
        assertTrue(runsPath.toFile().exists());
        assertTrue(runsPath.toFile().isDirectory());

        File[] runDirectories = runsPath.toFile().listFiles();
        assertEquals(1, runDirectories.length);

        File runDirectory = runDirectories[0];
        assertTrue(runDirectory.exists());
        assertTrue(runDirectory.isDirectory());

        File scanDirectory = new File(runDirectory, "scan");
        assertTrue(scanDirectory.exists());
        assertTrue(scanDirectory.isDirectory());

        File blackDuckScanOutput = new File(scanDirectory, "BlackDuckScanOutput");
        assertTrue(blackDuckScanOutput.exists());
        assertTrue(blackDuckScanOutput.isDirectory());

        File[] outputDirectories = blackDuckScanOutput.listFiles();
        assertEquals(1, outputDirectories.length);

        File outputDirectory = outputDirectories[0];
        assertTrue(outputDirectory.exists());
        assertTrue(outputDirectory.isDirectory());

        File dataDirectory = new File(outputDirectory, "data");
        assertTrue(dataDirectory.exists());
        assertTrue(dataDirectory.isDirectory());

        File[] dataFiles = dataDirectory.listFiles();
        assertEquals(1, dataFiles.length);
        assertTrue(dataFiles[0].length() > ONE_MILLION_BYTES);
    }

}
