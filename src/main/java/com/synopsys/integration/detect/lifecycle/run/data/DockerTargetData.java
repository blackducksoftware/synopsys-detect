package com.synopsys.integration.detect.lifecycle.run.data;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;

public class DockerTargetData {

    @Nullable
    private final File squashedImage;
    @Nullable
    private final File containerFilesystem;
    @Nullable
    private final File providedImageTar;

    public static DockerTargetData NO_DOCKER_TARGET = new DockerTargetData(null, null, null);

    public DockerTargetData(@Nullable File squashedImage, @Nullable File containerFilesystem, @Nullable File providedImageTar) {
        this.squashedImage = squashedImage;
        this.containerFilesystem = containerFilesystem;
        this.providedImageTar = providedImageTar;
    }

    public static DockerTargetData fromExtraction(Extraction extraction) {
        File squashedImage = extraction.getMetaData(DockerExtractor.SQUASHED_IMAGE_META_DATA).orElse(null);
        File containerFilesystem = extraction.getMetaData(DockerExtractor.CONTAINER_FILESYSTEM_META_DATA).orElse(null);
        File providedImageTar = extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).orElse(null);

        return new DockerTargetData(squashedImage, containerFilesystem, providedImageTar);
    }

    public Optional<File> getSquashedImage() {
        return Optional.ofNullable(squashedImage);
    }

    public Optional<File> getContainerFilesystem() {
        return Optional.ofNullable(containerFilesystem);
    }

    public Optional<File> getProvidedImageTar() {
        return Optional.ofNullable(providedImageTar);
    }
}