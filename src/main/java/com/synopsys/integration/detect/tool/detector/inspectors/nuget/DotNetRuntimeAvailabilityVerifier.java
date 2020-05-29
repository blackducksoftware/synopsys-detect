package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class DotNetRuntimeAvailabilityVerifier {
    private static final String RUNTIME_FIRST_TOKEN_CANDIDATE = "Microsoft";
    private static final String[] RUNTIME_SECOND_TOKEN_CANDIDATES = { ".AspNetCore", ".NETCore" };
    private static final String[] RUNTIME_THIRD_TOKEN_CANDIDATES = { ".All", ".App" };
    private static final String RUNTIME_LAST_TOKEN_START_CHARACTER = "[";
    //    private static final String DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER = "<DOTNET_VERSION>";
    //    private static final String DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION = "Microsoft\\.(AspNetCore|NETCore){1}\\.(All|App){1}(\\s){1}"
    //                                                                             + DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER
    //                                                                             + "(\\.[0-9]+){0,2}(\\s){1}\\[.+\\]";

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
        return isRuntimeAvailable(runtimes, versionTokens);
    }

    public boolean isRuntimeAvailable(List<String> runtimes, Integer... versionTokens) {
        // Pattern runtimePattern = createRuntimePattern(versionTokens);
        String versionSearchString = StringUtils.join(versionTokens, ".");
        for (String runtime : runtimes) {
            Optional<String> optionalVersionString = extractVersionTokenFromRuntimeString(runtime)
                                                         .filter(runtimeVersionString -> runtimeVersionString.startsWith(versionSearchString));
            if (optionalVersionString.isPresent()) {
                return true;
            }
            //            Matcher runtimeMatcher = runtimePattern.matcher(runtime);
            //            if (runtimeMatcher.matches()) {
            //                return true;
            //            }
        }
        return false;
    }

    private Optional<String> extractVersionTokenFromRuntimeString(String runtime) {
        String modifiedRuntimeString = runtime;
        if (!modifiedRuntimeString.startsWith(RUNTIME_FIRST_TOKEN_CANDIDATE)) {
            return Optional.empty();
        }

        modifiedRuntimeString = StringUtils.substringAfter(runtime, RUNTIME_FIRST_TOKEN_CANDIDATE);

        Optional<String> optionalSecondToken = findSubstringCandidate(modifiedRuntimeString, RUNTIME_SECOND_TOKEN_CANDIDATES);
        if (optionalSecondToken.isPresent()) {
            modifiedRuntimeString = StringUtils.substringAfter(runtime, optionalSecondToken.get());
        } else {
            return Optional.empty();
        }

        Optional<String> optionalThirdToken = findSubstringCandidate(modifiedRuntimeString, RUNTIME_THIRD_TOKEN_CANDIDATES);
        if (optionalThirdToken.isPresent()) {
            modifiedRuntimeString = StringUtils.substringAfter(runtime, optionalThirdToken.get());
        } else {
            return Optional.empty();
        }

        if (!StringUtils.contains(modifiedRuntimeString, RUNTIME_LAST_TOKEN_START_CHARACTER)) {
            return Optional.empty();
        }

        modifiedRuntimeString = StringUtils.substringBefore(modifiedRuntimeString, RUNTIME_LAST_TOKEN_START_CHARACTER);
        return Optional.of(modifiedRuntimeString.trim());
    }

    private Optional<String> findSubstringCandidate(String str, String[] substringCandidates) {
        String foundSecondTokenCandidate = null;
        for (String secondTokenCandidate : substringCandidates) {
            if (str.startsWith(secondTokenCandidate)) {
                foundSecondTokenCandidate = secondTokenCandidate;
                break;
            }
        }
        return Optional.ofNullable(foundSecondTokenCandidate);
    }

    //    private Pattern createRuntimePattern(Integer... versionTokens) {
    //        String regexSafeVersion = StringUtils.join(versionTokens, "\\.");
    //        String runtimePatternWithVersion = DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION.replace(DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER, regexSafeVersion);
    //        return Pattern.compile(runtimePatternWithVersion);
    //    }
}
