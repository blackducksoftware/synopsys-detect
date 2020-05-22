package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class NugetRuntimeResolver {
    private static final String DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER = "<DOTNET_VERSION>";
    private static final String DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION = "Microsoft\\.(AspNetCore|NETCore){1}\\.(All|App){1}(\\s){1}"
                                                                             + DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER
                                                                             + "(\\.[0-9]+){0,2}(\\s){1}\\[.+\\]";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutableRunner executableRunner;
    private File workingDir;

    public NugetRuntimeResolver(ExecutableRunner executableRunner, File workingDir) {
        this.executableRunner = executableRunner;
        this.workingDir = workingDir;
    }

    public boolean isRuntimeAvailable(String semanticVersion) throws DetectableException {
        String[] versionTokenStrings = StringUtils.split(semanticVersion, '.');
        List<Integer> numericVersionTokens = Arrays.stream(versionTokenStrings)
                                                 .filter(NumberUtils::isDigits)
                                                 .map(NumberUtils::toInt)
                                                 .collect(Collectors.toList());

        if (numericVersionTokens.size() < 2) {
            logger.warn("Invalid semantic version parameter for dotnet runtime query");
            return false;
        }

        return isRuntimeAvailable(numericVersionTokens.get(0), numericVersionTokens.get(1));
    }

    public boolean isRuntimeAvailable(Integer majorVersion, Integer minorVersion) throws DetectableException {
        List<String> runtimes = listAvailableDotNetRuntimes();
        Pattern runtimePattern = createRuntimePattern(majorVersion, minorVersion);
        for (String runtime : runtimes) {
            Matcher runtimeMatcher = runtimePattern.matcher(runtime);
            if (runtimeMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public List<String> listAvailableDotNetRuntimes() throws DetectableException {
        try {
            ExecutableOutput runtimesOutput = executableRunner.execute(workingDir, "dotnet", "--list-runtimes");
            List<String> foundRuntimes = runtimesOutput.getStandardOutputAsList()
                                             .stream()
                                             .map(StringUtils::trimToEmpty)
                                             .filter(StringUtils::isNotBlank)
                                             .collect(Collectors.toList());
            logger.info("Found {} available dotnet runtimes", foundRuntimes.size());
            if (foundRuntimes.isEmpty()) {
                throw new DetectableException("No available dotnet runtimes");
            }
            return foundRuntimes;
        } catch (ExecutableRunnerException e) {
            throw new DetectableException("Could not determine available dotnet runtimes", e);
        }
    }

    private Pattern createRuntimePattern(Integer majorVersion, Integer minorVersion) {
        String regexSafeVersion = String.join("\\.", majorVersion.toString(), minorVersion.toString());
        String runtimePattern = DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION.replace(DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER, regexSafeVersion);
        return Pattern.compile(runtimePattern);
    }
}
