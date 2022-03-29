package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorInfo;
import com.synopsys.integration.detectable.detectables.docker.DockerProperties;
import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierGenerator;
import com.synopsys.integration.detectable.detectables.docker.model.DockerInspectorResults;
import com.synopsys.integration.detectable.detectables.docker.parser.DockerInspectorResultsFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DockerExtractorTest {
    private static File fakeContainerFileSystemFile;
    private static File fakeSquashedImageFile;
    private static File fakeDockerTarFile;
    private static File fakeResultsFile;

    @BeforeAll
    public static void setup() throws IOException {
        fakeContainerFileSystemFile = Files.createTempFile("DockerExtractorTest", "_containerfilesystem.tar.gz").toFile();
        fakeSquashedImageFile = Files.createTempFile("DockerExtractorTest", "_squashedimage.tar.gz").toFile();
        fakeDockerTarFile = Files.createTempFile("DockerExtractorTest", "_testDockerTarfile.tar").toFile();
        fakeResultsFile = Files.createTempFile("DockerExtractorTest", "_results.json").toFile();
    }

    @AfterAll
    public static void tearDown() {
        fakeContainerFileSystemFile.delete();
        fakeDockerTarFile.delete();
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testExtractImageReturningContainerFileSystem() throws ExecutableRunnerException, IOException {

        final String image = "ubuntu:latest";
        String imageId = null;
        String tar = null;
        DetectableExecutableRunner executableRunner = getDetectableExecutableRunner();

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, null, fakeResultsFile, executableRunner);

        assertEquals("ubuntu:latest", extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get());
        assertTrue(extraction.getMetaData(DockerExtractor.CONTAINER_FILESYSTEM_META_DATA).get().getName().endsWith("_containerfilesystem.tar.gz"));

        ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        Executable executableToVerify = executableArgumentCaptor.getValue();
        List<String> command = executableToVerify.getCommandWithArguments();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertTrue(command.get(3).startsWith("--spring.config.location="));
        assertTrue(command.get(3).endsWith("/application.properties"));
        assertEquals("--docker.image=ubuntu:latest", command.get(4));
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testExtractImageReturningSquashedImage() throws ExecutableRunnerException, IOException {

        final String image = "ubuntu:latest";
        String imageId = null;
        String tar = null;
        DetectableExecutableRunner executableRunner = getDetectableExecutableRunner();

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, fakeSquashedImageFile, fakeResultsFile, executableRunner);

        assertEquals("ubuntu:latest", extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get());
        assertTrue(extraction.getMetaData(DockerExtractor.SQUASHED_IMAGE_META_DATA).get().getName().endsWith("_squashedimage.tar.gz"));

        ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        Executable executableToVerify = executableArgumentCaptor.getValue();
        List<String> command = executableToVerify.getCommandWithArguments();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertTrue(command.get(3).startsWith("--spring.config.location="));
        assertTrue(command.get(3).endsWith("/application.properties"));
        assertEquals("--docker.image=ubuntu:latest", command.get(4));
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testExtractTarReturningContainerFileSystem() throws ExecutableRunnerException, IOException {

        String image = null;
        String imageId = null;
        String tar = fakeDockerTarFile.getAbsolutePath();
        DetectableExecutableRunner executableRunner = getDetectableExecutableRunner();

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, null, fakeResultsFile, executableRunner);

        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get().endsWith("testDockerTarfile.tar"));
        assertTrue(extraction.getMetaData(DockerExtractor.CONTAINER_FILESYSTEM_META_DATA).get().getName().endsWith("_containerfilesystem.tar.gz"));

        ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        Executable executableToVerify = executableArgumentCaptor.getValue();
        List<String> command = executableToVerify.getCommandWithArguments();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertTrue(command.get(3).startsWith("--spring.config.location="));
        assertTrue(command.get(3).endsWith("/application.properties"));
        assertTrue(command.get(4).startsWith("--docker.tar="));
        assertTrue(command.get(4).endsWith("testDockerTarfile.tar"));
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testExtractTarReturningOriginalTar() throws ExecutableRunnerException, IOException {

        String image = null;
        String imageId = null;
        String tar = fakeDockerTarFile.getAbsolutePath();
        DetectableExecutableRunner executableRunner = getDetectableExecutableRunner();

        Extraction extraction = extract(image, imageId, tar, null, null, fakeResultsFile, executableRunner);

        // No returned .tar.gz: scan given docker tar instead
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get().endsWith("testDockerTarfile.tar"));
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("testDockerTarfile.tar"));

        ArgumentCaptor<Executable> executableArgumentCaptor = ArgumentCaptor.forClass(Executable.class);
        Mockito.verify(executableRunner).execute(executableArgumentCaptor.capture());
        Executable executableToVerify = executableArgumentCaptor.getValue();
        List<String> command = executableToVerify.getCommandWithArguments();
        assertTrue(command.get(0).endsWith("/fake/test/java"));
        assertEquals("-jar", command.get(1));
        assertTrue(command.get(2).endsWith("/fake/test/dockerinspector.jar"));
        assertTrue(command.get(3).startsWith("--spring.config.location="));
        assertTrue(command.get(3).endsWith("/application.properties"));
        assertTrue(command.get(4).startsWith("--docker.tar="));
        assertTrue(command.get(4).endsWith("testDockerTarfile.tar"));
    }

    @NotNull
    private DetectableExecutableRunner getDetectableExecutableRunner() throws ExecutableRunnerException {
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        ExecutableOutput executableOutput = Mockito.mock(ExecutableOutput.class);
        Mockito.when(executableOutput.getReturnCode()).thenReturn(0);
        Mockito.when(executableRunner.execute(Mockito.any(Executable.class))).thenReturn(executableOutput);
        return executableRunner;
    }

    private DockerExtractor getMockDockerExtractor(DetectableExecutableRunner executableRunner, FileFinder fileFinder) {
        BdioTransformer bdioTransformer = Mockito.mock(BdioTransformer.class);
        ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        Gson gson = new Gson();
        DockerInspectorResultsFileParser dockerInspectorResultsFileParser = Mockito.mock(DockerInspectorResultsFileParser.class);
        DockerInspectorResults dockerInspectorResults = new DockerInspectorResults("returnedimage", "returnedtag", "returned message");
        Mockito.when(dockerInspectorResultsFileParser.parse(Mockito.anyString())).thenReturn(Optional.of(dockerInspectorResults));
        ImageIdentifierGenerator imageIdentifierGenerator = new ImageIdentifierGenerator();

        return new DockerExtractor(fileFinder, executableRunner, bdioTransformer, externalIdFactory, gson, dockerInspectorResultsFileParser, imageIdentifierGenerator);
    }

    private Extraction extract(
        String image,
        String imageId,
        String tar,
        File returnedContainerFileSystemFile,
        File returnedSquashedImageFile,
        File resultsFile,
        DetectableExecutableRunner executableRunner
    ) throws IOException, ExecutableRunnerException {
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        DockerExtractor dockerExtractor = getMockDockerExtractor(executableRunner, fileFinder);

        File directory = new File(".");
        File outputDirectory = new File("build/tmp/test/DockerExtractorTest");
        ExecutableTarget bashExe = null;
        ExecutableTarget javaExe = ExecutableTarget.forFile(new File("fake/test/java"));

        DockerInspectorInfo dockerInspectorInfo = Mockito.mock(DockerInspectorInfo.class);
        Mockito.when(fileFinder.findFile(outputDirectory, DockerExtractor.CONTAINER_FILESYSTEM_FILENAME_PATTERN)).thenReturn(returnedContainerFileSystemFile);
        Mockito.when(fileFinder.findFile(outputDirectory, DockerExtractor.SQUASHED_IMAGE_FILENAME_PATTERN)).thenReturn(returnedSquashedImageFile);
        Mockito.when(fileFinder.findFile(outputDirectory, DockerExtractor.RESULTS_FILENAME_PATTERN)).thenReturn(resultsFile);
        Mockito.when(dockerInspectorInfo.getDockerInspectorJar()).thenReturn(new File("fake/test/dockerinspector.jar"));

        return dockerExtractor.extract(directory, outputDirectory, bashExe, javaExe, image, imageId, tar, dockerInspectorInfo, Mockito.mock(DockerProperties.class));
    }

}
