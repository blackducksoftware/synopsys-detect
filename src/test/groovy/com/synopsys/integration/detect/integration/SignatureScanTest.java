package com.synopsys.integration.detect.integration;

import com.synopsys.integration.detect.Application;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class SignatureScanTest extends BlackDuckIntegrationTest {
    private static final long ONE_MILLION_BYTES = 1_000_000;

    @Test
    @ExtendWith(TempDirectory.class)
    public void testOfflineScanWithSnippetMatching(@TempDirectory.TempDir Path tempOutputDirectory) throws Exception {
        String projectName = "synopsys-detect-junit";
        String projectVersionName = "offline-scan";
        assertProjectVersionReady(projectName, projectVersionName);

        String[] detectArgs = new String[]{
                "--detect.output.path=" + tempOutputDirectory.toString(),
                "--detect.project.name=" + projectName,
                "--detect.project.version.name=" + projectVersionName,
                "--detect.blackduck.signature.scanner.snippet.matching=SNIPPET_MATCHING",
                "--detect.blackduck.signature.scanner.dry.run=true"
        };
        Application.main(detectArgs);

        assertDirectoryStructureForOfflineScan(tempOutputDirectory);
    }

    private void assertDirectoryStructureForOfflineScan(@TempDirectory.TempDir Path tempOutputDirectory) {
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
