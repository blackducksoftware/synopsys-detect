package com.synopsys.integration.detectable.detectables.gradle;

public class GradleInspectorOptions {
    private String gradleBuildCommand;

    public GradleInspectorOptions(final String gradleBuildCommand) {this.gradleBuildCommand = gradleBuildCommand;}

    public String getGradleBuildCommand() {
        return gradleBuildCommand;
    }
}
