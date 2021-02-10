/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorInfo;
import com.synopsys.integration.detectable.detectables.docker.DockerProperties;
import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierType;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DockerExtractorTest {
    private static File fakeContainerFileSystemFile;
    private static File fakeSquashedImageFile;
    private static File fakeDockerTarFile;

    @BeforeAll
    public static void setup() throws IOException {
        fakeContainerFileSystemFile = Files.createTempFile("DockerExtractorTest", "_containerfilesystem.tar.gz").toFile();
        fakeSquashedImageFile = Files.createTempFile("DockerExtractorTest", "_squashedimage.tar.gz").toFile();
        fakeDockerTarFile = Files.createTempFile("DockerExtractorTest", "_testDockerTarfile.tar").toFile();
    }

    @AfterAll
    public static void tearDown() {
        fakeContainerFileSystemFile.delete();
        fakeDockerTarFile.delete();
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testExtractImageReturningContainerFileSystem() throws ExecutableRunnerException {

        final String image = "ubuntu:latest";
        String imageId = null;
        String tar = null;
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, null, executableRunner);

        assertEquals("ubuntu:latest", extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get());
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("_containerfilesystem.tar.gz"));

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
    public void testExtractImageReturningSquashedImage() throws ExecutableRunnerException {

        final String image = "ubuntu:latest";
        String imageId = null;
        String tar = null;
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, fakeSquashedImageFile, executableRunner);

        assertEquals("ubuntu:latest", extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get());
        // When Detect gets both .tar.gz files back, should prefer the squashed image
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("_squashedimage.tar.gz"));

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
    public void testExtractTarReturningContainerFileSystem() throws ExecutableRunnerException {

        String image = null;
        String imageId = null;
        String tar = fakeDockerTarFile.getAbsolutePath();
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);

        Extraction extraction = extract(image, imageId, tar, fakeContainerFileSystemFile, null, executableRunner);

        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA).get().endsWith("testDockerTarfile.tar"));
        assertTrue(extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).get().getName().endsWith("_containerfilesystem.tar.gz"));

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
    public void testExtractTarReturningOriginalTar() throws ExecutableRunnerException {

        String image = null;
        String imageId = null;
        String tar = fakeDockerTarFile.getAbsolutePath();
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);

        Extraction extraction = extract(image, imageId, tar, null, null, executableRunner);

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

    @Test
    @DisabledOnOs(WINDOWS)
    public void testGetImageIdentifierFromOutputDirectoryIfImageIdPresent() throws URISyntaxException {
        String testString = "test";
        String imageIdArgument = "--docker.image.id=";
        String imageName = "ubuntu:latest";
        File outputDirectoryWithPopulatedResultsFile = new File(DockerExtractorTest.class.getClassLoader().getSystemResource("detectables/functional/docker/unit/outputDirectoryWithPopulatedResultsFile").toURI());
        File outputDirectoryWithNonPopulatedResultsFile = new File(DockerExtractorTest.class.getClassLoader().getSystemResource("detectables/functional/docker/unit/outputDirectoryWithNonPopulatedResultsFile").toURI());

        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(outputDirectoryWithPopulatedResultsFile, DockerExtractor.RESULTS_FILENAME_PATTERN)).thenReturn(new File(outputDirectoryWithPopulatedResultsFile, "results.json"));
        Mockito.when(fileFinder.findFile(outputDirectoryWithNonPopulatedResultsFile, DockerExtractor.RESULTS_FILENAME_PATTERN)).thenReturn(new File(outputDirectoryWithNonPopulatedResultsFile, "results.json"));

        DockerExtractor dockerExtractor = getMockDockerExtractor(executableRunner, fileFinder);

        assertEquals(imageName, dockerExtractor.getImageIdentifierFromOutputDirectoryIfImageIdPresent(outputDirectoryWithPopulatedResultsFile, testString, ImageIdentifierType.IMAGE_ID));
        assertEquals(testString, dockerExtractor.getImageIdentifierFromOutputDirectoryIfImageIdPresent(outputDirectoryWithPopulatedResultsFile, testString, ImageIdentifierType.IMAGE_NAME));
        assertEquals(testString, dockerExtractor.getImageIdentifierFromOutputDirectoryIfImageIdPresent(outputDirectoryWithNonPopulatedResultsFile, testString, ImageIdentifierType.IMAGE_ID));
    }

    private DockerExtractor getMockDockerExtractor(DetectableExecutableRunner executableRunner, FileFinder fileFinder) {
        BdioTransformer bdioTransformer = Mockito.mock(BdioTransformer.class);
        ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        Gson gson = new Gson();

        return new DockerExtractor(fileFinder, executableRunner, bdioTransformer, externalIdFactory, gson);
    }

    private Extraction extract(String image, String imageId, String tar,
        File returnedContainerFileSystemFile,
        File returnedSquashedImageFile,
        DetectableExecutableRunner executableRunner) {
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        DockerExtractor dockerExtractor = getMockDockerExtractor(executableRunner, fileFinder);

        File directory = new File(".");
        File outputDirectory = new File("build/tmp/test/DockerExtractorTest");
        ExecutableTarget bashExe = null;
        ExecutableTarget javaExe = ExecutableTarget.forFile(new File("fake/test/java"));

        DockerInspectorInfo dockerInspectorInfo = Mockito.mock(DockerInspectorInfo.class);
        Mockito.when(fileFinder.findFile(outputDirectory, DockerExtractor.CONTAINER_FILESYSTEM_FILENAME_PATTERN)).thenReturn(returnedContainerFileSystemFile);
        Mockito.when(fileFinder.findFile(outputDirectory, DockerExtractor.SQUASHED_IMAGE_FILENAME_PATTERN)).thenReturn(returnedSquashedImageFile);
        Mockito.when(dockerInspectorInfo.getDockerInspectorJar()).thenReturn(new File("fake/test/dockerinspector.jar"));

        return dockerExtractor.extract(directory, outputDirectory, bashExe, javaExe, image, imageId, tar, dockerInspectorInfo, Mockito.mock(DockerProperties.class));
    }

}
