/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerProperties {
    private final DockerDetectableOptions dockerDetectableOptions;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DockerProperties(DockerDetectableOptions dockerDetectableOptions) {
        this.dockerDetectableOptions = dockerDetectableOptions;
    }

    public void populatePropertiesFile(File dockerPropertiesFile, File outputDirectory) throws IOException {
        Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.synopsys", dockerDetectableOptions.getDockerInspectorLoggingLevel().toString()); //TODO: Verify this .toString works.
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("phone.home", "false");
        dockerProperties.setProperty("caller.name", "Detect");
        dockerProperties.setProperty("working.dir.path", createDir(outputDirectory, "inspectorWorkingDir").getAbsolutePath());
        dockerProperties.setProperty("shared.dir.path.local", createDir(outputDirectory, "inspectorSharedDir").getAbsolutePath());

        // Request both of the following; DI pre-8.1.0 will only recognize/return containerfilesystem.
        // DI 8.1.0 and newer will provide both; Detect will prefer squashedimage
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("output.include.squashedimage", "true");
        dockerDetectableOptions.getDockerPlatformTopLayerId().ifPresent(id -> {
            dockerProperties.setProperty("docker.platform.top.layer.id", id);
        });

        Map<String, String> additionalDockerProperties = dockerDetectableOptions.getAdditionalDockerProperties();
        dockerProperties.putAll(additionalDockerProperties);

        logger.debug("Contents of application.properties passed to Docker Inspector: " + dockerProperties.toString());
        try (FileOutputStream fileOutputStream = new FileOutputStream(dockerPropertiesFile)) {
            dockerProperties.store(fileOutputStream, "");
        }
    }

    private File createDir(File parentDir, String newDirName) throws IOException {
        File newDir = new File(parentDir, newDirName);
        Files.createDirectories(newDir.toPath());
        return newDir;
    }
}
