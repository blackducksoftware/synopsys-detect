package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;

public class NugetExeVersionResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<Version> resolveVersion(final String[] nugetPackageRepos, String inspectorName, String nugetConfig, File workingDirectory, final String nugetExecutablePath, ExecutableRunner executableRunner)
        throws ExecutableRunnerException {
        for (String repo : nugetPackageRepos) {
            for (final String source : nugetPackageRepos) {
                logger.debug("Attempting source: " + source);
                final Optional<Version> inspectorVersion = resolveVersion(source, inspectorName, nugetConfig, workingDirectory, nugetExecutablePath, executableRunner);
                if (inspectorVersion.isPresent()) {
                    logger.debug(String.format("Found version [%s] in source [%s]", inspectorVersion, source));
                    return inspectorVersion;
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Version> resolveVersion(final String nugetRepository, String inspectorName, String nugetConfig, File workingDirectory, final String nugetExecutablePath, ExecutableRunner executableRunner)
        throws ExecutableRunnerException {
        Optional<Version> detectVersion = Optional.empty();

        final List<String> nugetOptions = new ArrayList<>(Arrays.asList(
            "list",
            inspectorName,
            "-Source",
            nugetRepository)
        );

        if (StringUtils.isNotBlank(nugetConfig)) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfig);
        }

        final Executable getInspectorVersionExecutable = new Executable(workingDirectory, nugetExecutablePath, nugetOptions);

        final List<String> output = executableRunner.execute(getInspectorVersionExecutable).getStandardOutputAsList();
        for (final String line : output) {
            final String[] lineChunks = line.split(" ");
            if (inspectorName.equalsIgnoreCase(lineChunks[0])) {
                detectVersion = Optional.of(Version.valueOf(lineChunks[1]));
            }
        }

        return detectVersion;
    }
}
