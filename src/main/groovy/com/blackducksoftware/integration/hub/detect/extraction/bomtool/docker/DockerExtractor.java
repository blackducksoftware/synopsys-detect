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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.BdioReader;
import com.blackducksoftware.integration.hub.bdio.BdioTransformer;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableArgumentBuilder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.util.ResourceUtil;
import com.google.gson.Gson;

@Component
public class DockerExtractor extends Extractor<DockerContext> {
    public static final String TAR_FILENAME_PATTERN = "*.tar.gz";
    public static final String DEPENDENCIES_PATTERN = "*bdio.jsonld";

    private final Logger logger = LoggerFactory.getLogger(DockerExtractor.class);

    @Autowired
    protected DetectFileFinder detectFileFinder;

    @Autowired
    protected DetectFileManager detectFileManager;

    @Autowired
    DockerProperties dockerProperties;

    @Autowired
    ExecutableManager executableManager;

    @Autowired
    ExecutableRunner executableRunner;

    @Autowired
    BdioTransformer bdioTransformer;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Autowired
    Gson gson;

    @Autowired
    HubSignatureScanner hubSignatureScanner;

    @Override
    public Extraction extract(final DockerContext context) {
        try {
            String imageArgument = null;
            String imagePiece = null;
            if (StringUtils.isNotBlank(context.tar)) {
                final File dockerTarFile = new File(context.tar);
                imageArgument = String.format("--docker.tar=%s", dockerTarFile.getCanonicalPath());
                imagePiece = detectFileFinder.extractFinalPieceFromPath(dockerTarFile.getCanonicalPath());
            } else if (StringUtils.isNotBlank(context.image)) {
                imagePiece = context.image;
                imageArgument = String.format("--docker.image=%s", context.image);
            }

            if (StringUtils.isBlank(imageArgument) || StringUtils.isBlank(imagePiece)) {
                return new Extraction.Builder().failure("No docker image found.").build();
            } else {
                return executeDocker(context, imageArgument, imagePiece, context.tar, context.directory, context.dockerExe, context.bashExe, context.dockerInspectorInfo);
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Map<String, String> createEnvironmentVariables(final DockerContext context, final File dockerExe) throws IOException {
        final Map<String, String> environmentVariables = new HashMap<>();
        dockerProperties.populateEnvironmentVariables(context, environmentVariables, dockerExe);
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

    private Extraction executeDocker(final DockerContext context, final String imageArgument, final String imagePiece, final String dockerTarFilePath, final File directory, final File dockerExe, final File bashExe,
            final DockerInspectorInfo dockerInspectorInfo)
            throws FileNotFoundException, IOException, ExecutableRunnerException {

        final File outputDirectory = detectFileManager.getOutputDirectory(context);
        final File dockerPropertiesFile = detectFileManager.getOutputFile(context, "application.properties");
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, outputDirectory);

        final Map<String, String> environmentVariables = createEnvironmentVariables(context, dockerExe);

        final List<String> dockerArguments = new ArrayList<>();
        // The -c is a bash option, the following String is the command we want to run
        dockerArguments.add("-c");

        final ExecutableArgumentBuilder bashArguments = new ExecutableArgumentBuilder();
        bashArguments.addArgument(dockerInspectorInfo.dockerInspectorScript.getCanonicalPath(), true);
        bashArguments.addArgumentPair("--spring.config.location", "file:" + dockerPropertiesFile.getCanonicalPath(), true);
        bashArguments.addArgument(imageArgument);

        if (dockerInspectorInfo.isOffline) {
            bashArguments.insertArgumentPair(2, "--dry.run", "true");
            bashArguments.insertArgumentPair(3, "--no.prompt", "true");
            bashArguments.insertArgumentPair(4, "--jar.path", dockerInspectorInfo.offlineDockerInspectorJar.getCanonicalPath(), true);
            importTars(dockerInspectorInfo.offlineDockerInspectorJar, dockerInspectorInfo.offlineTars, outputDirectory, environmentVariables, bashExe);
        }

        // All the arguments should be joined into a single String, as the command to run after the -c
        dockerArguments.add(bashArguments.buildString());

        final Executable dockerExecutable = new Executable(outputDirectory, environmentVariables, bashExe.toString(), dockerArguments);
        executableRunner.execute(dockerExecutable);

        final File producedTarFile = detectFileFinder.findFile(outputDirectory, TAR_FILENAME_PATTERN);
        if (null != producedTarFile && producedTarFile.isFile()) {
            hubSignatureScanner.setDockerTarFile(producedTarFile);
        } else {
            logger.debug(String.format("No files found matching pattern [%s]. Expected docker-inspector to produce file in %s", TAR_FILENAME_PATTERN, outputDirectory.getCanonicalPath()));
            if (StringUtils.isNotBlank(dockerTarFilePath)) {
                final File dockerTarFile = new File(dockerTarFilePath);
                if (dockerTarFile.isFile()) {
                    logger.debug(String.format("Will scan the provided Docker tar file %s", dockerTarFile.getCanonicalPath()));
                    hubSignatureScanner.setDockerTarFile(dockerTarFile);
                }
            }
        }

        return findCodeLocations(outputDirectory, directory, imagePiece);
    }

    private Extraction findCodeLocations(final File directoryToSearch, final File directory, final String imageName) {
        final File bdioFile = detectFileFinder.findFile(directoryToSearch, DEPENDENCIES_PATTERN);
        if (bdioFile != null) {
            SimpleBdioDocument simpleBdioDocument = null;
            BdioReader bdioReader = null;
            InputStream dockerOutputInputStream = null;

            try {
                dockerOutputInputStream = new FileInputStream(bdioFile);
                bdioReader = new BdioReader(gson, dockerOutputInputStream);
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            } catch (final Exception e) {
            } finally {
                ResourceUtil.closeQuietly(bdioReader);
                ResourceUtil.closeQuietly(dockerOutputInputStream);
            }

            if (simpleBdioDocument != null) {
                final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.project, simpleBdioDocument.components);

                final String projectName = simpleBdioDocument.project.name;
                final String projectVersionName = simpleBdioDocument.project.version;

                final Forge dockerForge = new Forge(ExternalId.BDIO_ID_SEPARATOR, ExternalId.BDIO_ID_SEPARATOR, simpleBdioDocument.project.bdioExternalIdentifier.forge);
                final String externalIdPath = simpleBdioDocument.project.bdioExternalIdentifier.externalId;
                final ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath);

                final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.DOCKER, directory.toString(), projectExternalId, dependencyGraph).dockerImage(imageName).build();

                return new Extraction.Builder().success(detectCodeLocation).projectName(projectName).projectVersion(projectVersionName).build();
            }
        }

        return new Extraction.Builder().failure("No files found matching pattern [" + DEPENDENCIES_PATTERN + "]. Expected docker-inspector to produce file in " + directory.toString()).build();
    }

}
