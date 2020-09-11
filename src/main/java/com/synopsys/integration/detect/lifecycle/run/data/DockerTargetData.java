/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.util.NameVersion;

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

    public DockerTargetData(Extraction extraction) {
        this.squashedImage = extraction.getMetaData(DockerExtractor.SQUASHED_IMAGE_META_DATA).orElse(null);
        this.containerFilesystem = extraction.getMetaData(DockerExtractor.CONTAINER_FILESYSTEM_META_DATA).orElse(null);
        this.providedImageTar = extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).orElse(null);
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
