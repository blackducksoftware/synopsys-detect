/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;

public class DockerTargetData {

    @Nullable
    private File squashedImage;
    @Nullable
    private File containerFilesystem;
    @Nullable
    private File providedImageTar;

    public static DockerTargetData NO_DOCKER_TARGET = new DockerTargetData(null, null, null);

    public DockerTargetData(@Nullable final File squashedImage, @Nullable final File containerFilesystem, @Nullable File providedImageTar) {
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