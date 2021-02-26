/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.docker.model.DockerImageInfo;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionMetadata;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

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
    private final DetectableExecutableRunner executableRunner;
    private final BdioTransformer bdioTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    private ImageIdentifierType imageIdentifierType;

    public DockerExtractor(FileFinder fileFinder, DetectableExecutableRunner executableRunner, BdioTransformer bdioTransformer, ExternalIdFactory externalIdFactory, Gson gson) {
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bdioTransformer = bdioTransformer;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public Extraction extract(File directory, File outputDirectory, ExecutableTarget dockerExe, ExecutableTarget javaExe, String image, String imageId, String tar, DockerInspectorInfo dockerInspectorInfo,
        DockerProperties dockerProperties) {
        try {
            String imageArgument = null;
            String imagePiece = null;
            if (StringUtils.isNotBlank(tar)) {
                File dockerTarFile = new File(tar);
                imageArgument = String.format("--docker.tar=%s", dockerTarFile.getCanonicalPath());
                imagePiece = dockerTarFile.getName();
                imageIdentifierType = ImageIdentifierType.TAR;
            } else if (StringUtils.isNotBlank(image)) {
                imagePiece = image;
                imageArgument = String.format("--docker.image=%s", image);
                imageIdentifierType = ImageIdentifierType.IMAGE_NAME;
            } else if (StringUtils.isNotBlank(imageId)) {
                imagePiece = imageId;
                imageArgument = String.format("--docker.image.id=%s", imageId);
                imageIdentifierType = ImageIdentifierType.IMAGE_ID;
            }

            if (StringUtils.isBlank(imageArgument) || StringUtils.isBlank(imagePiece)) {
                return new Extraction.Builder().failure("No docker image found.").build();
            } else {
                return executeDocker(outputDirectory, imageArgument, imagePiece, tar, directory, javaExe, dockerExe, dockerInspectorInfo, dockerProperties);
            }
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void importTars(List<File> importTars, File directory, Map<String, String> environmentVariables, ExecutableTarget dockerExe) {
        try {
            for (File imageToImport : importTars) {
                loadDockerImage(directory, environmentVariables, dockerExe, imageToImport);
            }
        } catch (Exception e) {
            logger.debug(String.format("Exception encountered when resolving paths for docker air gap: %s", e.getMessage()));
            logger.debug("Running in online mode instead");
        }
    }

    private void loadDockerImage(File directory, Map<String, String> environmentVariables, ExecutableTarget dockerExe, File imageToImport) throws IOException, ExecutableRunnerException, IntegrationException {
        List<String> dockerImportArguments = Arrays.asList(
            "load",
            "-i",
            imageToImport.getCanonicalPath());
        Executable dockerImportImageExecutable = ExecutableUtils.createFromTarget(directory, environmentVariables, dockerExe, dockerImportArguments);
        ExecutableOutput exeOut = executableRunner.execute(dockerImportImageExecutable);
        if (exeOut.getReturnCode() != 0) {
            throw new IntegrationException(String.format("Command %s %s returned %d: %s",
                dockerExe.toCommand(), dockerImportArguments,
                exeOut.getReturnCode(), exeOut.getErrorOutput()));
        }
    }

    private Extraction executeDocker(File outputDirectory, String imageArgument, String suppliedImagePiece, String dockerTarFilePath, File directory, ExecutableTarget javaExe, ExecutableTarget dockerExe,
        DockerInspectorInfo dockerInspectorInfo, DockerProperties dockerProperties)
        throws IOException, ExecutableRunnerException {

        File dockerPropertiesFile = new File(outputDirectory, "application.properties");
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, outputDirectory);
        Map<String, String> environmentVariables = new HashMap<>(0);
        List<String> dockerArguments = new ArrayList<>();
        dockerArguments.add("-jar");
        dockerArguments.add(dockerInspectorInfo.getDockerInspectorJar().getAbsolutePath());
        dockerArguments.add("--spring.config.location=file:" + dockerPropertiesFile.getCanonicalPath());
        dockerArguments.add(imageArgument);
        if (dockerInspectorInfo.hasAirGapImageFiles()) {
            importTars(dockerInspectorInfo.getAirGapInspectorImageTarFiles(), outputDirectory, environmentVariables, dockerExe);
        }
        Executable dockerExecutable = ExecutableUtils.createFromTarget(outputDirectory, environmentVariables, javaExe, dockerArguments);
        executableRunner.execute(dockerExecutable);

        File scanFile = null;
        File producedSquashedImageFile = fileFinder.findFile(outputDirectory, SQUASHED_IMAGE_FILENAME_PATTERN);
        File producedContainerFileSystemFile = fileFinder.findFile(outputDirectory, CONTAINER_FILESYSTEM_FILENAME_PATTERN);
        if (null != producedSquashedImageFile && producedSquashedImageFile.isFile()) {
            logger.debug(String.format("Will signature scan: %s", producedSquashedImageFile.getAbsolutePath()));
            scanFile = producedSquashedImageFile;
        } else if (null != producedContainerFileSystemFile && producedContainerFileSystemFile.isFile()) {
            logger.debug(String.format("Will signature scan: %s", producedContainerFileSystemFile.getAbsolutePath()));
            scanFile = producedContainerFileSystemFile;
        } else {
            logger.debug(String.format("No files found matching pattern [%s]. Expected docker-inspector to produce file in %s", CONTAINER_FILESYSTEM_FILENAME_PATTERN, outputDirectory.getCanonicalPath()));
            if (StringUtils.isNotBlank(dockerTarFilePath)) {
                File dockerTarFile = new File(dockerTarFilePath);
                if (dockerTarFile.isFile()) {
                    logger.debug(String.format("Will scan the provided Docker tar file %s", dockerTarFile.getCanonicalPath()));
                    scanFile = dockerTarFile;
                }
            }
        }

        Extraction.Builder extractionBuilder = findCodeLocations(outputDirectory, directory);
        String imageIdentifier = getImageIdentifierFromOutputDirectoryIfImageIdPresent(outputDirectory, suppliedImagePiece, imageIdentifierType);
        extractionBuilder.metaData(DOCKER_TAR_META_DATA, scanFile).metaData(DOCKER_IMAGE_NAME_META_DATA, imageIdentifier);
        return extractionBuilder.build();
    }

    private Extraction.Builder findCodeLocations(File directoryToSearch, File directory) {
        File bdioFile = fileFinder.findFile(directoryToSearch, DEPENDENCIES_PATTERN);
        if (bdioFile != null) {
            SimpleBdioDocument simpleBdioDocument = null;

            try (InputStream dockerOutputInputStream = new FileInputStream(bdioFile); BdioReader bdioReader = new BdioReader(gson, dockerOutputInputStream)) {
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            } catch (Exception e) {
                return new Extraction.Builder().exception(e);
            }

            if (simpleBdioDocument != null) {
                DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.getProject(), simpleBdioDocument.getComponents());

                String projectName = simpleBdioDocument.getProject().name;
                String projectVersionName = simpleBdioDocument.getProject().version;

                // TODO ejk - update this when project external id is not req'd anymore
                Forge dockerForge = new Forge(BdioId.BDIO_ID_SEPARATOR, simpleBdioDocument.getProject().bdioExternalIdentifier.forge);
                String externalIdPath = simpleBdioDocument.getProject().bdioExternalIdentifier.externalId;
                ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath);

                CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph, projectExternalId);

                return new Extraction.Builder().success(detectCodeLocation).projectName(projectName).projectVersion(projectVersionName);
            }
        }

        return new Extraction.Builder().failure("No files found matching pattern [" + DEPENDENCIES_PATTERN + "]. Expected docker-inspector to produce file in " + directory.toString());
    }

    public String getImageIdentifierFromOutputDirectoryIfImageIdPresent(File outputDirectory, String suppliedImagePiece, ImageIdentifierType imageIdentifierType) {
        File producedResultFile = fileFinder.findFile(outputDirectory, RESULTS_FILENAME_PATTERN);
        if (imageIdentifierType.equals(ImageIdentifierType.IMAGE_ID) && producedResultFile != null) {
            String jsonText;
            try {
                jsonText = FileUtils.readFileToString(producedResultFile, StandardCharsets.UTF_8);
                DockerImageInfo dockerImageInfo = gson.fromJson(jsonText, DockerImageInfo.class);
                String imageRepo = dockerImageInfo.getImageRepo();
                String imageTag = dockerImageInfo.getImageTag();
                if (StringUtils.isNotBlank(imageRepo) && StringUtils.isNotBlank(imageTag)) {
                    return imageRepo + ":" + imageTag;
                }
            } catch (IOException | JsonSyntaxException e) {
                logger.debug("Failed to parse results file from run of Docker Inspector, thus could not get name of image.  The code location name for this scan will be derived from the passed image ID");
            }
        }
        return suppliedImagePiece;
    }

}
