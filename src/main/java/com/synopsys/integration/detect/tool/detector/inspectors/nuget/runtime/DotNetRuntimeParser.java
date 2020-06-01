package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DotNetRuntimeParser {
    private static final String[] RUNTIME_PREFIX_END_TOKENS = { "All", "App" };
    private static final String RUNTIME_SUFFIX_START_TOKEN = "[";

    public boolean doesRuntimeContainVersionStartingWith(List<String> runtimes, String versionSearchString) {
        return runtimes
                   .stream()
                   .map(this::extractVersionSubstringFromRuntime)
                   .anyMatch(extractedVersionToken -> extractedVersionToken.startsWith(versionSearchString));
    }

    private String extractVersionSubstringFromRuntime(String runtime) {
        for (String lastTokenBeforeVersion : RUNTIME_PREFIX_END_TOKENS) {
            if (runtime.contains(lastTokenBeforeVersion)) {
                runtime = StringUtils.substringAfter(runtime, lastTokenBeforeVersion);
                break;
            }
        }
        runtime = StringUtils.substringBefore(runtime, RUNTIME_SUFFIX_START_TOKEN);
        return runtime.trim();
    }
}
