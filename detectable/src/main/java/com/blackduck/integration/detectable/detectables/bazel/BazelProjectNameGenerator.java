package com.blackduck.integration.detectable.detectables.bazel;

public class BazelProjectNameGenerator {

    public String generateFromBazelTarget(String bazelTarget) {
        return bazelTarget
            .replaceAll("^//", "")
            .replaceAll("^:", "")
            .replace("/", "_")
            .replace(":", "_");
    }
}
