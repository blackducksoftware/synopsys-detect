package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse.YarnListParser;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse.YarnLockParser;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class YarnLockExtractor extends Extractor<YarnLockContext> {
    private final Logger logger = LoggerFactory.getLogger(YarnLockExtractor.class);
    public static final String OUTPUT_FILE = "detect_yarn_proj_dependencies.txt";
    public static final String ERROR_FILE = "detect_yarn_error.txt";

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Autowired
    DetectConfiguration detectConfiguration;

    @Autowired
    YarnLockParser yarnLockParser;

    @Autowired
    YarnListParser yarnListParser;

    @Autowired
    DetectFileManager detectFileManager;

    @Autowired
    ExecutableRunner executableRunner;

    @Override
    public Extraction extract(final YarnLockContext context) {
        try {
            final List<String> yarnLockText = Files.readAllLines(context.yarnlock.toPath(), StandardCharsets.UTF_8);

            if (detectConfiguration.getYarnProductionDependenciesOnly() && StringUtils.isBlank(context.yarnExe)) {
                return new Extraction.Builder().failure("Could not find the Yarn executable, can not get the production only dependencies.").build();
            }

            DependencyGraph dependencyGraph;
            if (detectConfiguration.getYarnProductionDependenciesOnly()) {
                final List<String> yarnListLines = executeYarnList(context);
                dependencyGraph = yarnListParser.parseYarnList(yarnLockText, yarnListLines);
            } else {
                dependencyGraph = yarnLockParser.parseYarnLock(yarnLockText);
            }

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, context.directory.getCanonicalPath());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.YARN, context.directory.getCanonicalPath(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    List<String> executeYarnList(final YarnLockContext context) throws ExecutableRunnerException, IOException {
        final File yarnListOutputFile = detectFileManager.getOutputFile(context, OUTPUT_FILE);
        final File yarnListErrorFile = detectFileManager.getOutputFile(context, ERROR_FILE);

        final List<String> exeArgs = Arrays.asList("list", "--prod");

        final Executable yarnListExe = new Executable(detectFileManager.getOutputDirectory(context), context.yarnExe, exeArgs);
        executableRunner.executeToFile(yarnListExe, yarnListOutputFile, yarnListErrorFile);

        if (!(yarnListOutputFile.length() > 0)) {
            if (yarnListErrorFile.length() > 0) {
                logger.error("Error when running yarn list --prod command");
                logger.debug(Files.readAllLines(yarnListErrorFile.toPath(), StandardCharsets.UTF_8).stream().collect(Collectors.joining(System.lineSeparator())));
            } else {
                logger.warn("Nothing returned from yarn list --prod command");
            }
        }

        return Files.readAllLines(yarnListOutputFile.toPath(), StandardCharsets.UTF_8);
    }


}