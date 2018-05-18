package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.parse.NpmCliDependencyFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.parse.NpmParseResult;
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
            return new Extraction.Builder().exception(e).build();
        }

        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command");
                printError(npmLsErrorFile);
                return new Extraction.Builder().failure("Npm returned no output after runnin npm ls.").build();
            }
            try {
                final NpmParseResult result = npmCliDependencyFinder.generateCodeLocation(context.directory.getCanonicalPath(), npmLsOutputFile);
                return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
            } catch (final IOException e) {
                return new Extraction.Builder().exception(e).build();
            }

        } else if (npmLsErrorFile.length() > 0) {
            logger.error("Error when running npm ls -json command");
            printError(npmLsErrorFile);
            return new Extraction.Builder().failure("Npm returned error after running npm ls.").build();
        } else {
            logger.warn("Nothing returned from npm ls -json command");
            return new Extraction.Builder().failure("Npm returned nothing after running npm ls").build();
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
