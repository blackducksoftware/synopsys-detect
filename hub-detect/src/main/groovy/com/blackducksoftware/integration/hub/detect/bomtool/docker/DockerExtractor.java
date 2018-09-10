/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.docker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableArgumentBuilder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckSignatureScanner;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.BdioReader;
import com.synopsys.integration.hub.bdio.BdioTransformer;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class DockerExtractor {
    public static final String TAR_FILENAME_PATTERN = "*.tar.gz";
    public static final String DEPENDENCIES_PATTERN = "*bdio.jsonld";

    private final Logger logger = LoggerFactory.getLogger(DockerExtractor.class);

    private final DetectFileFinder detectFileFinder;
    private final DetectFileManager detectFileManager;
    private final DockerProperties dockerProperties;
    private final ExecutableRunner executableRunner;
    private final BdioTransformer bdioTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;
    private final BlackDuckSignatureScanner blackDuckSignatureScanner;

    public DockerExtractor(final DetectFileFinder detectFileFinder, final DetectFileManager detectFileManager, final DockerProperties dockerProperties,
            final ExecutableRunner executableRunner, final BdioTransformer bdioTransformer, final ExternalIdFactory externalIdFactory, final Gson gson, final BlackDuckSignatureScanner blackDuckSignatureScanner) {
        this.detectFileFinder = detectFileFinder;
        this.detectFileManager = detectFileManager;
        this.dockerProperties = dockerProperties;
        this.executableRunner = executableRunner;
        this.bdioTransformer = bdioTransformer;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
        this.blackDuckSignatureScanner = blackDuckSignatureScanner;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final ExtractionId extractionId, final File bashExe, final File dockerExe, final String image, final String tar,
            final DockerInspectorInfo dockerInspectorInfo) {
        try {
            String imageArgument = null;
            String imagePiece = null;
            if (StringUtils.isNotBlank(tar)) {
                final File dockerTarFile = new File(tar);
                imageArgument = String.format("--docker.tar=\"%s\"", dockerTarFile.getCanonicalPath());
                imagePiece = detectFileFinder.extractFinalPieceFromPath(dockerTarFile.getCanonicalPath());
            } else if (StringUtils.isNotBlank(image)) {
                imagePiece = image;
                imageArgument = String.format("--docker.image=%s", image);
            }

            if (StringUtils.isBlank(imageArgument) || StringUtils.isBlank(imagePiece)) {
                return new Extraction.Builder().failure("No docker image found.").build();
            } else {
                return executeDocker(bomToolType, extractionId, imageArgument, imagePiece, tar, directory, dockerExe, bashExe, dockerInspectorInfo);
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Map<String, String> createEnvironmentVariables(final String dockerInspectorVersion, final File dockerExe) throws IOException {
        final Map<String, String> environmentVariables = new HashMap<>();
        dockerProperties.populateEnvironmentVariables(dockerInspectorVersion, environmentVariables, dockerExe);
        return environmentVariables;
    }

    private void importTars(final File inspectorJar, final List<File> importTars, final File directory, final Map<String, String> environmentVariables, final File bashExe) {
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

    private Extraction executeDocker(final BomToolType bomToolType, final ExtractionId extractionId, final String imageArgument, final String imagePiece, final String dockerTarFilePath, final File directory, final File dockerExe,
            final File bashExe,
            final DockerInspectorInfo dockerInspectorInfo)
            throws FileNotFoundException, IOException, ExecutableRunnerException {

        final File outputDirectory = detectFileManager.getOutputDirectory(extractionId);
        final File dockerPropertiesFile = detectFileManager.getOutputFile(outputDirectory, "application.properties");
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, outputDirectory);

        String dockerInspectorVersion = "";

        // we want to get the resolved Docker inspector version, if Detect was able to determine a version
        dockerInspectorVersion = dockerInspectorInfo.version;

        final Map<String, String> environmentVariables = createEnvironmentVariables(dockerInspectorVersion, dockerExe);

        final List<String> dockerArguments = new ArrayList<>();
        // The -c is a bash option, the following String is the command we want to run
        dockerArguments.add("-c");

        final ExecutableArgumentBuilder bashArguments = new ExecutableArgumentBuilder();
        bashArguments.addArgument(dockerInspectorInfo.dockerInspectorScript.getCanonicalPath(), true);
        bashArguments.addArgumentPair("--spring.config.location", "file:" + dockerPropertiesFile.getCanonicalPath(), true);
        bashArguments.addArgument(imageArgument);

        if (dockerInspectorInfo.isOffline) {
            bashArguments.insertArgumentPair(2, "--jar.path", dockerInspectorInfo.offlineDockerInspectorJar.getCanonicalPath(), true);
            importTars(dockerInspectorInfo.offlineDockerInspectorJar, dockerInspectorInfo.offlineTars, outputDirectory, environmentVariables, bashExe);
        }

        // All the configuration should be joined into a single String, as the command to run after the -c
        dockerArguments.add(bashArguments.buildString());

        final Executable dockerExecutable = new Executable(outputDirectory, environmentVariables, bashExe.toString(), dockerArguments);
        executableRunner.execute(dockerExecutable);

        final File producedTarFile = detectFileFinder.findFile(outputDirectory, TAR_FILENAME_PATTERN);
        if (null != producedTarFile && producedTarFile.isFile()) {
            blackDuckSignatureScanner.setDockerTarFile(producedTarFile);
        } else {
            logger.debug(String.format("No files found matching pattern [%s]. Expected docker-inspector to produce file in %s", TAR_FILENAME_PATTERN, outputDirectory.getCanonicalPath()));
            if (StringUtils.isNotBlank(dockerTarFilePath)) {
                final File dockerTarFile = new File(dockerTarFilePath);
                if (dockerTarFile.isFile()) {
                    logger.debug(String.format("Will scan the provided Docker tar file %s", dockerTarFile.getCanonicalPath()));
                    blackDuckSignatureScanner.setDockerTarFile(dockerTarFile);
                }
            }
        }

        return findCodeLocations(bomToolType, outputDirectory, directory, imagePiece);
    }

    private Extraction findCodeLocations(final BomToolType bomToolType, final File directoryToSearch, final File directory, final String imageName) {
        final File bdioFile = detectFileFinder.findFile(directoryToSearch, DEPENDENCIES_PATTERN);
        if (bdioFile != null) {
            SimpleBdioDocument simpleBdioDocument = null;

            try (final InputStream dockerOutputInputStream = new FileInputStream(bdioFile); BdioReader bdioReader = new BdioReader(gson, dockerOutputInputStream)) {
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            } catch (final Exception e) {
                return new Extraction.Builder().exception(e).build();
            }

            if (simpleBdioDocument != null) {
                final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.project, simpleBdioDocument.components);

                final String projectName = simpleBdioDocument.project.name;
                final String projectVersionName = simpleBdioDocument.project.version;

                final Forge dockerForge = new Forge(ExternalId.BDIO_ID_SEPARATOR, ExternalId.BDIO_ID_SEPARATOR, simpleBdioDocument.project.bdioExternalIdentifier.forge);
                final String externalIdPath = simpleBdioDocument.project.bdioExternalIdentifier.externalId;
                final ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath);

                final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.DOCKER, bomToolType, directory.toString(), projectExternalId, dependencyGraph).dockerImage(imageName).build();

                return new Extraction.Builder().success(detectCodeLocation).projectName(projectName).projectVersion(projectVersionName).build();
            }
        }

        return new Extraction.Builder().failure("No files found matching pattern [" + DEPENDENCIES_PATTERN + "]. Expected docker-inspector to produce file in " + directory.toString()).build();
    }

}
