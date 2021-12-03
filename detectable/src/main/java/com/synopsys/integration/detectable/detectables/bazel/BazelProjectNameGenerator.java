package com.synopsys.integration.detectable.detectables.bazel;

public class BazelProjectNameGenerator {

    public String generateFromBazelTarget(String bazelTarget) {
        String projectName = bazelTarget
            .replaceAll("^//", "")
            .replaceAll("^:", "")
            .replace("/", "_")
            .replace(":", "_");
        return projectName;
    }
}
