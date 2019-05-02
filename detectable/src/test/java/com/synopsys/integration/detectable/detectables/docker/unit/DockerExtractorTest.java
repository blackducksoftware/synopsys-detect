package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorInfo;
import com.synopsys.integration.detectable.detectables.docker.DockerProperties;

public class DockerExtractorTest {
    private static File fakeContainerFileSystemFile;

    @BeforeAll
    public static void setup() throws IOException {
        fakeContainerFileSystemFile = Files.createTempFile("DockerExtractorTest", "testExtract.tar.gz").toFile();

    }

    @AfterAll
    public static void tearDown() {
        fakeContainerFileSystemFile.delete();
    }

    @Test
    public void testExtractContainerFileSystem() throws IOException {
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

        assertEquals("-Dblackduck.scan.cli.hashwhitelist=*", extraction.getMetaData(DockerExtractor.DOCKER_SCAN_JAVA_OPTION_META_DATA).get());
    }

    @Test
    public void testExtractDockerTar() throws IOException {
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
        Mockito.when(fileFinder.findFile(outputDirectory, "*.tar.gz")).thenReturn(null);
        Mockito.when(dockerInspectorInfo.getDockerInspectorJar()).thenReturn(new File("fake/test/dockerinspector.jar"));

        final Extraction extraction = dockerExtractor.extract(directory, outputDirectory, bashExe, javaExe, image, tar, dockerInspectorInfo);

        assertFalse(extraction.getMetaData(DockerExtractor.DOCKER_SCAN_JAVA_OPTION_META_DATA).isPresent());
    }
}
