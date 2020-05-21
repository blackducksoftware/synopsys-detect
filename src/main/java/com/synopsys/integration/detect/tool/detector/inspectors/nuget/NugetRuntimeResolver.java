package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class NugetRuntimeResolver {
    private static final String DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER = "<VERSION>";
    private static final String DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION = "Microsoft\\.(AspNetCore|NETCore){1}\\.(All|App){1}(\\s){1}"
                                                                             + DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER + "(\\.[0-9]+){0,2}(\\s){1}\\[.+\\]";

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
        // TODO implement
        throw new DetectableException("No available dotnet runtimes");
    }

    private Pattern createRuntimePattern(Integer majorVersion, Integer minorVersion) {
        String regexSafeVersion = String.join("\\.", majorVersion.toString(), minorVersion.toString());
        String runtimePattern = DOTNET_RUNTIME_PATTERN_WITHOUT_VERSION.replace(DOTNET_RUNTIME_PATTERN_VERSION_PLACEHOLDER, regexSafeVersion);
        return Pattern.compile(runtimePattern);
    }
}
