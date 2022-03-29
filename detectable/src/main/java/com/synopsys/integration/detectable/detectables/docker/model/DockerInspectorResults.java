package com.synopsys.integration.detectable.detectables.docker.model;

public class DockerInspectorResults {
    private final String imageRepo;
    private final String imageTag;
    private final String message;

    public DockerInspectorResults(String imageRepo, String imageTag, String message) {
        this.imageRepo = imageRepo;
        this.imageTag = imageTag;
        this.message = message;
    }

    public String getImageRepo() {
        return imageRepo;
    }

    public String getImageTag() {
        return imageTag;
    }

    public String getMessage() {
        return message;
    }
}
