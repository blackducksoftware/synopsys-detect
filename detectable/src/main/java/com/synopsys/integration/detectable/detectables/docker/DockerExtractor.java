/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionMetadata;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class DockerExtractor {
    public static final ExtractionMetadata<File> DOCKER_TAR_META_DATA = new ExtractionMetadata<>("dockerTar", File.class);
    public static final ExtractionMetadata<String> DOCKER_IMAGE_NAME_META_DATA = new ExtractionMetadata<>("dockerImage", String.class);
    public static final ExtractionMetadata<String> DOCKER_IMAGE_ID_META_DATA = new ExtractionMetadata<>("dockerImageId", String.class);

    public static final String CONTAINER_FILESYSTEM_FILENAME_PATTERN = "*_containerfilesystem.tar.gz";
    public static final String SQUASHED_IMAGE_FILENAME_PATTERN = "*_squashedimage.tar.gz";
    public static final String RESULTS_FILENAME_PATTERN = "results.json";
    public static final String DEPENDENCIES_PATTERN = "*bdio.jsonld";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final DockerProperties dockerProperties;
    private final ExecutableRunner executableRunner;
    private final BdioTransformer bdioTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    public DockerExtractor(final FileFinder fileFinder, final DockerProperties dockerProperties, final ExecutableRunner executableRunner, final BdioTransformer bdioTransformer, final ExternalIdFactory externalIdFactory, final Gson gson) {
        this.fileFinder = fileFinder;
        this.dockerProperties = dockerProperties;
        this.executableRunner = executableRunner;
        this.bdioTransformer = bdioTransformer;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public Extraction extract(final File directory, final File outputDirectory, final File bashExe, final File javaExe, final String image, final String imageId, final String tar, final DockerInspectorInfo dockerInspectorInfo) {
        try {
            String imageArgument = null;
            String imagePiece = null;
            if (StringUtils.isNotBlank(tar)) {
                final File dockerTarFile = new File(tar);
                imageArgument = String.format("--docker.tar=%s", dockerTarFile.getCanonicalPath());
                imagePiece = dockerTarFile.getName();
            } else if (StringUtils.isNotBlank(image)) {
                imagePiece = image;
                imageArgument = String.format("--docker.image=%s", image);
            } else if (StringUtils.isNotBlank(imageId)) {
                //imagePiece = getImagePieceFromOutputDirectory(outputDirectory);
                imagePiece = imageId;
                imageArgument = String.format("--docker.image.id=%s", imageId);
            }

            if (StringUtils.isBlank(imageArgument) || StringUtils.isBlank(imagePiece)) {
                return new Extraction.Builder().failure("No docker image found.").build();
            } else {
                return executeDocker(outputDirectory, imageArgument, imagePiece, tar, directory, javaExe, bashExe, dockerInspectorInfo);
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void importTars(final List<File> importTars, final File directory, final Map<String, String> environmentVariables, final File bashExe) {
        try {
            for (final File imageToImport : importTars) {
                // The -c is a bash option, the following String is the command we want to run
                final List<String> dockerImportArguments = Arrays.asList(
                    "-c",
                    "docker load -i \"" + imageToImport.getCanonicalPath() + "\"");

                final Executable dockerImportImageExecutable = new Executable(directory, environmentVariables, bashExe.toString(), dockerImportArguments);
                executableRunner.execute(dockerImportImageExecutable);
            }
        } catch (final Exception e) {
            logger.debug("Exception encountered when resolving paths for docker air gap, running in online mode instead");
            logger.debug(e.getMessage());
        }
    }

    private Extraction executeDocker(final File outputDirectory, final String imageArgument, String imagePiece, final String dockerTarFilePath, final File directory, final File javaExe, final File bashExe,
        final DockerInspectorInfo dockerInspectorInfo)
        throws IOException, ExecutableRunnerException {

        final File dockerPropertiesFile = new File(outputDirectory, "application.properties");
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, outputDirectory);
        final Map<String, String> environmentVariables = new HashMap<>(0);
        final List<String> dockerArguments = new ArrayList<>();
        dockerArguments.add("-jar");
        dockerArguments.add(dockerInspectorInfo.getDockerInspectorJar().getAbsolutePath());
        dockerArguments.add("--spring.config.location=file:" + dockerPropertiesFile.getCanonicalPath());
        dockerArguments.add(imageArgument);
        if (dockerInspectorInfo.hasAirGapImageFiles()) {
            importTars(dockerInspectorInfo.getAirGapInspectorImageTarFiles(), outputDirectory, environmentVariables, bashExe);
        }
        final Executable dockerExecutable = new Executable(outputDirectory, environmentVariables, javaExe.getAbsolutePath(), dockerArguments);
        executableRunner.execute(dockerExecutable);

        File scanFile = null;
        final File producedSquashedImageFile = fileFinder.findFile(outputDirectory, SQUASHED_IMAGE_FILENAME_PATTERN);
        final File producedContainerFileSystemFile = fileFinder.findFile(outputDirectory, CONTAINER_FILESYSTEM_FILENAME_PATTERN);
        if (null != producedSquashedImageFile && producedSquashedImageFile.isFile()) {
            logger.debug(String.format("Will signature scan: %s", producedSquashedImageFile.getAbsolutePath()));
            scanFile = producedSquashedImageFile;
        } else if (null != producedContainerFileSystemFile && producedContainerFileSystemFile.isFile()) {
            logger.debug(String.format("Will signature scan: %s", producedContainerFileSystemFile.getAbsolutePath()));
            scanFile = producedContainerFileSystemFile;
        } else {
            logger.debug(String.format("No files found matching pattern [%s]. Expected docker-inspector to produce file in %s", CONTAINER_FILESYSTEM_FILENAME_PATTERN, outputDirectory.getCanonicalPath()));
            if (StringUtils.isNotBlank(dockerTarFilePath)) {
                final File dockerTarFile = new File(dockerTarFilePath);
                if (dockerTarFile.isFile()) {
                    logger.debug(String.format("Will scan the provided Docker tar file %s", dockerTarFile.getCanonicalPath()));
                    scanFile = dockerTarFile;
                }
            }
        }

        final Extraction.Builder extractionBuilder = findCodeLocations(outputDirectory, directory);
        imagePiece = getImagePieceFromOutputDirectoryIfImageIdPresent(outputDirectory, imageArgument, imagePiece);
        extractionBuilder.metaData(DOCKER_TAR_META_DATA, scanFile).metaData(DOCKER_IMAGE_NAME_META_DATA, imagePiece);
        return extractionBuilder.build();
    }

    private Extraction.Builder findCodeLocations(final File directoryToSearch, final File directory) {
        final File bdioFile = fileFinder.findFile(directoryToSearch, DEPENDENCIES_PATTERN);
        if (bdioFile != null) {
            SimpleBdioDocument simpleBdioDocument = null;

            try (final InputStream dockerOutputInputStream = new FileInputStream(bdioFile); final BdioReader bdioReader = new BdioReader(gson, dockerOutputInputStream)) {
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            } catch (final Exception e) {
                return new Extraction.Builder().exception(e);
            }

            if (simpleBdioDocument != null) {
                final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.getProject(), simpleBdioDocument.getComponents());

                final String projectName = simpleBdioDocument.getProject().name;
                final String projectVersionName = simpleBdioDocument.getProject().version;

                // TODO ejk - update this when project external id is not req'd anymore
                final Forge dockerForge = new Forge(BdioId.BDIO_ID_SEPARATOR, simpleBdioDocument.getProject().bdioExternalIdentifier.forge);
                final String externalIdPath = simpleBdioDocument.getProject().bdioExternalIdentifier.externalId;
                final ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath);

                final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph, projectExternalId);

                return new Extraction.Builder().success(detectCodeLocation).projectName(projectName).projectVersion(projectVersionName);
            }
        }

        return new Extraction.Builder().failure("No files found matching pattern [" + DEPENDENCIES_PATTERN + "]. Expected docker-inspector to produce file in " + directory.toString());
    }

    private String getImagePieceFromOutputDirectoryIfImageIdPresent(File outputDirectory, String imageArgument, String imagePiece) {
        if (imageArgument.contains("image.id")) {
            final File producedResultFile = fileFinder.findFile(outputDirectory, RESULTS_FILENAME_PATTERN);
            String jsonText = null;
            try {
                jsonText = FileUtils.readFileToString(producedResultFile, StandardCharsets.UTF_8);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            DockerImageInfo dockerImageInfo = gson.fromJson(jsonText, DockerImageInfo.class);

            return dockerImageInfo.getImageRepo() + ":" + dockerImageInfo.getImageTag();
        } else {
            return imagePiece;
        }
    }

}
