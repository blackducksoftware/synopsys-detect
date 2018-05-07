package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class NpmCliExtractor extends Extractor<NpmCliContext>  {
    private final Logger logger = LoggerFactory.getLogger(NpmCliExtractor.class);

    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private ExecutableRunner executableRunner;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private NpmCliDependencyFinder npmCliDependencyFinder;

    @Override
    public Extraction extract(final NpmCliContext context) {

        final File npmLsOutputFile = detectFileManager.getOutputFile(context, NpmCliExtractor.OUTPUT_FILE);
        final File npmLsErrorFile = detectFileManager.getOutputFile(context, NpmCliExtractor.ERROR_FILE);

        final boolean includeDevDeps = detectConfiguration.getNpmIncludeDevDependencies();
        final List<String> exeArgs = Arrays.asList("ls", "-json");
        if (!includeDevDeps) {
            exeArgs.add("-prod");
        }

        final Executable npmLsExe = new Executable(context.directory, context.npmExe, exeArgs);
        try {
            executableRunner.executeToFile(npmLsExe, npmLsOutputFile, npmLsErrorFile);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }

        final List<DetectCodeLocation> codeLocations = new ArrayList<>();
        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command");
                printError(npmLsErrorFile);
                return new Extraction(ExtractionResult.Failure);
            }
            DetectCodeLocation detectCodeLocation;
            try {
                detectCodeLocation = npmCliDependencyFinder.generateCodeLocation(context.directory.getCanonicalPath(), npmLsOutputFile);
                codeLocations.add(detectCodeLocation);
                return new Extraction(ExtractionResult.Success, codeLocations);
            } catch (final IOException e) {
                return new Extraction(ExtractionResult.Failure, e);
            }

        } else if (npmLsErrorFile.length() > 0) {
            logger.error("Error when running npm ls -json command");
            printError(npmLsErrorFile);
            return new Extraction(ExtractionResult.Failure);
        } else {
            logger.warn("Nothing returned from npm ls -json command");
            return new Extraction(ExtractionResult.Failure);
        }


    }

    void printError(final File errorFile) {
        String text = "";
        try {
            for (final String line : Files.readAllLines(errorFile.toPath(), StandardCharsets.UTF_8)){
                text += line + System.lineSeparator();
            }
        } catch (final IOException e) {
            logger.debug("Failed to read NPM error file.");
        }
        logger.debug(text);
    }

}
