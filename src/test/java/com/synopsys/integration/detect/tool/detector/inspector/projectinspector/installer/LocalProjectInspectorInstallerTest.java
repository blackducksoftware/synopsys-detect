package com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class LocalProjectInspectorInstallerTest {

    @Test
    void testNonExistent() {
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = Mockito.mock(ProjectInspectorExecutableLocator.class);
        Path projectInspectorZipPath = Paths.get("/thisdoesnotexist");
        ProjectInspectorInstaller localProjectInspectorInstaller = new LocalProjectInspectorInstaller(projectInspectorExecutableLocator, projectInspectorZipPath);

        try {
            localProjectInspectorInstaller.install(new File("/tmp/destinationDir"));
            fail("Expected Exception for non-existent file");
        } catch (DetectableException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }
}
