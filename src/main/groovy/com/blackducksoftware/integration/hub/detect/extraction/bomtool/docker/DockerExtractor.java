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

import org.apache.commons.io.IOUtils;
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
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableArgumentBuilder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.google.gson.Gson;

@Component
public class DockerExtractor extends Extractor<DockerContext> {
    private final Logger logger = LoggerFactory.getLogger(DockerExtractor.class);

    @Autowired
    DockerProperties dockerProperties;

    @Autowired
    DetectFileManager detectFileManager;

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

    static final String DEPENDENCIES_PATTERN = "*bdio.jsonld";

    @Override
    public Extraction extract(final DockerContext context) {
        try {
            String imageArgument = null;
            String imagePiece = null;
            if (context.tar != null) {
                final File dockerTarFile = new File(context.tar);
                imageArgument = String.format("--docker.tar=%s", dockerTarFile.getCanonicalPath());
                imagePiece = detectFileManager.extractFinalPieceFromPath(dockerTarFile.getCanonicalPath());
            }else if (context.image != null) {
                imagePiece = context.image;
                imageArgument = String.format("--docker.image=%s", context.image);
            }

            if (imageArgument == null || imagePiece == null){
                return new Extraction(ExtractionResult.Failure);
            }else {
                final List<DetectCodeLocation> codeLocations = executeDocker(imageArgument, imagePiece, context.directory, context.dockerExe, context.bashExe, context.dockerInspectorInfo);
                return new Extraction(ExtractionResult.Success, codeLocations);
            }
        }catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }

    private Map<String, String> createEnvironmentVariables(final File dockerExe) throws IOException {
        final Map<String, String> environmentVariables = new HashMap<>();
        dockerProperties.populateEnvironmentVariables(environmentVariables, dockerExe);
        return environmentVariables;
    }

    private void importTars(final File inspectorJar, final List<File> importTars, final File directory, final Map<String, String> environmentVariables, final File bashExe) {
        try {
            for (final File imageToImport : importTars) {
                final List<String> dockerImportArguments = Arrays.asList(
                        "-c",
                        "docker load -i \"" + imageToImport.getCanonicalPath() + "\""
                        );

                final Executable dockerImportImageExecutable = new Executable(directory, environmentVariables, bashExe.toString(), dockerImportArguments);
                executableRunner.execute(dockerImportImageExecutable);
            }
        } catch (final Exception e) {
            logger.debug("Exception encountered when resolving paths for docker air gap, running in online mode instead");
            logger.debug(e.getMessage());
        }
    }



    private  List<DetectCodeLocation> executeDocker(final String imageArgument, final String imagePiece, final File directory, final File dockerExe, final File bashExe, final DockerInspectorInfo dockerInspectorInfo) throws FileNotFoundException, IOException, ExecutableRunnerException {

        final File dockerPropertiesFile = detectFileManager.createFile(BomToolType.DOCKER, "application.properties");
        final File dockerBomToolDirectory =  dockerPropertiesFile.getParentFile();
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, dockerBomToolDirectory);

        final Map<String, String> environmentVariables = createEnvironmentVariables(dockerExe);

        final ExecutableArgumentBuilder bashArguments = new ExecutableArgumentBuilder();
        bashArguments.addArgument("-c");
        bashArguments.addArgument(dockerInspectorInfo.dockerInspectorScript.getCanonicalPath(), true);
        bashArguments.addArgumentPair("--spring.config.location", "file:" + dockerProperties.toString(), true);
        bashArguments.addArgument(imageArgument);

        if (!dockerInspectorInfo.isOffline) {
            bashArguments.insertArgumentPair(2, "--dry.run", "true");
            bashArguments.insertArgumentPair(3, "--no.prompt", "true");
            bashArguments.insertArgumentPair(4, "--jar.path", dockerInspectorInfo.offlineDockerInspectorJar.getCanonicalPath(), true);
            importTars(dockerInspectorInfo.offlineDockerInspectorJar, dockerInspectorInfo.offlineTars, dockerBomToolDirectory, environmentVariables, bashExe);
        }

        final Executable dockerExecutable = new Executable(dockerBomToolDirectory, environmentVariables, bashExe.toString(), bashArguments.build());
        executableRunner.execute(dockerExecutable);

        return findCodeLocations(dockerBomToolDirectory, directory, imagePiece);
    }

    private List<DetectCodeLocation> findCodeLocations(final File directoryToSearch, final File directory, final String imageName) {
        final List<DetectCodeLocation> codeLocations = new ArrayList<>();
        final File bdioFile = detectFileManager.findFile(directoryToSearch, DEPENDENCIES_PATTERN);
        if (bdioFile != null) {
            SimpleBdioDocument simpleBdioDocument = null;
            BdioReader bdioReader = null;
            try {
                final InputStream dockerOutputInputStream = new FileInputStream(bdioFile);
                bdioReader = new BdioReader(gson, dockerOutputInputStream);
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            } catch (final Exception e) {

            } finally {
                IOUtils.closeQuietly(bdioReader);
            }

            final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.project, simpleBdioDocument.components);

            final String projectName = simpleBdioDocument.project.name;
            final String projectVersionName = simpleBdioDocument.project.version;

            final Forge dockerForge = new Forge(ExternalId.BDIO_ID_SEPARATOR, ExternalId.BDIO_ID_SEPARATOR, simpleBdioDocument.project.bdioExternalIdentifier.forge);
            final String externalIdPath = simpleBdioDocument.project.bdioExternalIdentifier.externalId;
            final ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath);

            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.DOCKER, directory.toString(), projectExternalId, dependencyGraph).bomToolProjectName(projectName).bomToolProjectVersionName(projectVersionName).dockerImage(imageName).build();
            codeLocations.add(detectCodeLocation);
        } else {
            logMissingFile(directoryToSearch, DEPENDENCIES_PATTERN);
        }

        return codeLocations;
    }

    private void logMissingFile(final File searchDirectory, final String filenamePattern) {
        logger.debug("No files found matching pattern [${filenamePattern}]. Expected docker-inspector to produce file in ${searchDirectory.getCanonicalPath()}");
    }


}
