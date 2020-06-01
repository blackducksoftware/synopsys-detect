package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class DotNetRuntimeParser {
    private static final String[] RUNTIME_PREFIX_CANDIDATES = {
        "Microsoft.AspNetCore.All",
        "Microsoft.AspNetCore.App",
        "Microsoft.NETCore.All",
        "Microsoft.NETCore.App"
    };
    private static final String RUNTIME_LAST_TOKEN_START_CHARACTER = "[";

    public boolean doesRuntimeContainVersionStartingWith(List<String> runtimes, String versionSearchString) {
        return runtimes
                   .stream()
                   .map(this::extractVersionTokenFromRuntimeString)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .anyMatch(extractedVersionToken -> extractedVersionToken.startsWith(versionSearchString));
    }

    private Optional<String> extractVersionTokenFromRuntimeString(String runtime) {
        String modifiedRuntimeString = runtime;

        Optional<String> optionalSubstringToken = findRuntimePrefix(modifiedRuntimeString);
        if (optionalSubstringToken.isPresent()) {
            modifiedRuntimeString = StringUtils.substringAfter(modifiedRuntimeString, optionalSubstringToken.get());

            if (StringUtils.contains(modifiedRuntimeString, RUNTIME_LAST_TOKEN_START_CHARACTER)) {
                modifiedRuntimeString = StringUtils.substringBefore(modifiedRuntimeString, RUNTIME_LAST_TOKEN_START_CHARACTER);
                return Optional.of(modifiedRuntimeString.trim());
            }
        }
        return Optional.empty();
    }

    private Optional<String> findRuntimePrefix(String runtime) {
        for (String prefixCandidate : RUNTIME_PREFIX_CANDIDATES) {
            if (runtime.startsWith(prefixCandidate)) {
                return Optional.of(prefixCandidate);
            }
        }
        return Optional.empty();
    }
}
