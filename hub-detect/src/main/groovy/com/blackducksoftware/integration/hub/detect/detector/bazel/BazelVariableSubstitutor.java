package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BazelVariableSubstitutor {

    private final Map<String, String> substitutions;

    public BazelVariableSubstitutor(final String bazelTarget, final String bazelTargetDependencyId) {
        substitutions = new HashMap<>(2);
        substitutions.put("XXXdetect.bazel.targetXXX", bazelTarget);
        substitutions.put("XXXdetect.bazel.target.dependencyXXX", bazelTargetDependencyId);
    }

    public List<String> substitute(final List<String> origStrings) {
        final List<String> modifiedStrings = new ArrayList<>(origStrings.size());
        for (String origString : origStrings) {
            modifiedStrings.add(substitute(origString));
        }
        return modifiedStrings;
    }

    private String substitute(final String origString) {
        String modifiedString = origString;
        for (String variablePattern : substitutions.keySet()) {
            modifiedString = modifiedString.replaceAll(variablePattern, substitutions.get(variablePattern));
        }
        return modifiedString;
    }
}
