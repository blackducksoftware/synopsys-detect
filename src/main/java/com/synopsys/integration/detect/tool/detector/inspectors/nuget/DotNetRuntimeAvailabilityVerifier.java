package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

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

public class DotNetRuntimeAvailabilityVerifier {
    private static final String DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER = "<DOTNET_VERSION>";
    private static final String DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION = "Microsoft\\.(AspNetCore|NETCore){1}\\.(All|App){1}(\\s){1}"
                                                                             + DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER
                                                                             + "(\\.[0-9]+){0,2}(\\s){1}\\[.+\\]";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DotNetRuntimeFinder dotNetRuntimeFinder;

    public DotNetRuntimeAvailabilityVerifier(DotNetRuntimeFinder dotNetRuntimeFinder) {
        this.dotNetRuntimeFinder = dotNetRuntimeFinder;
    }

    public boolean isRuntimeAvailable(String semanticVersion) throws DetectableException {
        String[] versionTokenStrings = StringUtils.split(semanticVersion, '.');
        List<Integer> numericVersionTokens = Arrays.stream(versionTokenStrings)
                                                 .filter(NumberUtils::isDigits)
                                                 .map(NumberUtils::toInt)
                                                 .collect(Collectors.toList());

        if (numericVersionTokens.isEmpty()) {
            logger.warn("Invalid semantic version parameter for dotnet runtime query");
            return false;
        }

        Integer[] intArray = new Integer[numericVersionTokens.size()];
        return isRuntimeAvailable(numericVersionTokens.toArray(intArray));
    }

    public boolean isRuntimeAvailable(Integer... versionTokens) throws DetectableException {
        List<String> runtimes = dotNetRuntimeFinder.listAvailableRuntimes();
        Pattern runtimePattern = createRuntimePattern(versionTokens);
        for (String runtime : runtimes) {
            Matcher runtimeMatcher = runtimePattern.matcher(runtime);
            if (runtimeMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    private Pattern createRuntimePattern(Integer... versionTokens) {
        String regexSafeVersion = StringUtils.join(versionTokens, "\\.");
        String runtimePatternWithVersion = DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION.replace(DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER, regexSafeVersion);
        return Pattern.compile(runtimePatternWithVersion);
    }
}
