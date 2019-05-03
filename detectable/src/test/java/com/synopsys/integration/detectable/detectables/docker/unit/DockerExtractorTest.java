package com.synopsys.integration.detectable.detectables.docker.unit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorInfo;
import com.synopsys.integration.detectable.detectables.docker.DockerProperties;

public class DockerExtractorTest {
    private static File fakeContainerFileSystemFile;
    private static File fakeDockerTarFile;

    @BeforeAll
    public static void setup() throws IOException {
        fakeContainerFileSystemFile = Files.createTempFile("DockerExtractorTest", "_testExtract.tar.gz").toFile();
        fakeDockerTarFile = Files.createTempFile("DockerExtractorTest", "_testDockerTarfile.tar").toFile();
    }

    @AfterAll
    public static void tearDown() {
        fakeContainerFileSystemFile.delete();
        fakeDockerTarFile.delete();
    }

    @Test
    public void testExtractContainerFileSystem() throws IOException, ExecutableRunnerException {
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final DockerProperties dockerProperties = Mockito.mock(DockerProperties.class);
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final BdioTransformer bdioTransformer = Mockito.mock(BdioTransformer.class);
        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        final Gson gson = new Gson();

        final DockerExtractor dockerExtractor = new DockerExtractor(fileFinder, dockerProperties, executableRunner, bdioTransformer, externalIdFactory, gson);

        final File directory = new File(".");
        final File outputDirectory = new File("build/tmp/test/DockerExtractorTest");
        final File bashExe = null;
        final File javaExe = new File("fake/test/java");
        final String image = "ubuntu:latest";
        final String tar = null;
        final DockerInspectorInfo dockerInspectorInfo = Mockito.mock(DockerInspectorInfo.class);
        Mockito.when(fileFinder.findFile(outputDirectory, "*.tar.gz")).thenReturn(fakeContainerFileSystemFile);
        Mockito.when(dockerInspectorInfo.getDockerInspectorJar()).thenReturn(new File("fake/test/dockerinspector.jar"));

        final Extraction extraction = dockerExtractor.extract(directory, outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);

        assertEquals("ubuntu:latest", extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get());
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("testExtract.tar.gz"));

        final ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        final Executable executableToVerify = executableArgumentCaptor.getValue();
        final List<String> command = executableToVerify.getCommand();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertEquals("--spring.config.location", command.get(3));
        assertTrue(command.get(4).endsWith("/application.properties"));
        assertEquals("--docker.image=ubuntu:latest", command.get(5));
    }

    @Test
    public void testExtractDockerTar() throws IOException, ExecutableRunnerException {
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final DockerProperties dockerProperties = Mockito.mock(DockerProperties.class);
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final BdioTransformer bdioTransformer = Mockito.mock(BdioTransformer.class);
        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        final Gson gson = new Gson();

        final DockerExtractor dockerExtractor = new DockerExtractor(fileFinder, dockerProperties, executableRunner, bdioTransformer, externalIdFactory, gson);

        final File directory = new File(".");
        final File outputDirectory = new File("build/tmp/test/DockerExtractorTest");
        final File bashExe = null;
        final File javaExe = new File("fake/test/java");
        final String image = null;
        final String tar = fakeDockerTarFile.getAbsolutePath();
        final DockerInspectorInfo dockerInspectorInfo = Mockito.mock(DockerInspectorInfo.class);
        Mockito.when(fileFinder.findFile(outputDirectory, "*.tar.gz")).thenReturn(null);
        Mockito.when(dockerInspectorInfo.getDockerInspectorJar()).thenReturn(new File("fake/test/dockerinspector.jar"));

        final Extraction extraction = dockerExtractor.extract(directory, outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);

        // This looks strange, but it looks like this must be this way for code location name derivation:
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get().endsWith("testDockerTarfile.tar"));
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("testDockerTarfile.tar"));

        final ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        final Executable executableToVerify = executableArgumentCaptor.getValue();
        final List<String> command = executableToVerify.getCommand();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertEquals("--spring.config.location", command.get(3));
        assertTrue(command.get(4).endsWith("/application.properties"));
        assertTrue(command.get(5).startsWith("--docker.tar="));
        assertTrue(command.get(5).endsWith("testDockerTarfile.tar"));
    }
}
