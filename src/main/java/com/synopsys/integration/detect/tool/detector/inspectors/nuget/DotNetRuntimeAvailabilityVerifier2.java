package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DotNetRuntimeAvailabilityVerifier2 {
    private static final String[] VERSION_PREFIXES = new String[] {
            "Microsoft.AspNetCore.All"
            ,"Microsoft.AspNetCore.App"
            ,"Microsoft.NETCore.All"
            ,"Microsoft.NETCore.App"
    };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DotNetRuntimeFinder dotNetRuntimeFinder;

    public DotNetRuntimeAvailabilityVerifier2(DotNetRuntimeFinder dotNetRuntimeFinder) {
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
        String versionSearchString = StringUtils.join(versionTokens, ".");
        for (String runtime : runtimes) {
            String versionString = extractVersionTokenFromRuntimeString(runtime);
            if (versionString.startsWith(versionSearchString)) {
                return true;
            }
        }
        return false;
    }

    private String extractVersionTokenFromRuntimeString(String runtime) {
        String modifiedRuntimeString = runtime;
        for (String prefix : VERSION_PREFIXES) {
            modifiedRuntimeString = modifiedRuntimeString.replace(prefix, "");
        }

        int bracketIndex = modifiedRuntimeString.indexOf("[");
        modifiedRuntimeString = modifiedRuntimeString.substring(0, bracketIndex).trim();

        return modifiedRuntimeString;
    }

}
