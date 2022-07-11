package com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.util.OperatingSystemType;

public class LocalProjectInspectorInstallerTest {

    @Test
    void testValidZipFile(@TempDir Path tempDir) throws DetectableException {
        DetectInfo detectInfo = Mockito.mock(DetectInfo.class);
        Mockito.when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.LINUX);
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = new ProjectInspectorExecutableLocator(detectInfo);
        Path projectInspectorZipPath = Paths.get("src/test/resources/tool/detector/inspector/projectinspector/installer/project-inspector-fake.zip");
        ProjectInspectorInstaller localProjectInspectorInstaller = new LocalProjectInspectorInstaller(projectInspectorExecutableLocator, projectInspectorZipPath);
        File exeFile = localProjectInspectorInstaller.install(tempDir.toFile());
        assertTrue(exeFile.exists());
    }

    @Test
    void testNonExistentZipFile() {
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = Mockito.mock(ProjectInspectorExecutableLocator.class);
        Path projectInspectorZipPath = Paths.get("/thisfiledoesnotexistonanysystem");
        ProjectInspectorInstaller localProjectInspectorInstaller = new LocalProjectInspectorInstaller(projectInspectorExecutableLocator, projectInspectorZipPath);

        try {
            localProjectInspectorInstaller.install(new File("/tmp/destinationDir"));
            fail("Expected Exception for non-existent file");
        } catch (DetectableException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }
}
