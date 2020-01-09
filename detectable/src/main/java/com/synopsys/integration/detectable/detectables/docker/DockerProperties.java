/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerProperties {
    private final DockerDetectableOptions dockerDetectableOptions;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DockerProperties(final DockerDetectableOptions dockerDetectableOptions) {
        this.dockerDetectableOptions = dockerDetectableOptions;
    }

    public void populatePropertiesFile(final File dockerPropertiesFile, final File outputDirectory) throws IOException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.synopsys", dockerDetectableOptions.getDockerInspectorLoggingLevel().toString()); //TODO: Verify this .toString works.
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("phone.home", "false");
        dockerProperties.setProperty("caller.name", "Detect");

        // Request both of the following; DI pre-8.1.0 will only recognize/return containerfilesystem.
        // DI 8.1.0 and newer will provide both; Detect will prefer squashedimage
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("output.include.squashedimage", "true");
        if (StringUtils.isNotBlank(dockerDetectableOptions.getDockerPlatformTopLayerId())) {
            dockerProperties.setProperty("docker.platform.top.layer.id", dockerDetectableOptions.getDockerPlatformTopLayerId());
        }

        final Map<String, String> additionalDockerProperties = dockerDetectableOptions.getAdditionalDockerProperties();
        dockerProperties.putAll(additionalDockerProperties);

        logger.debug("Contents of application.properties passed to Docker Inspector: " + dockerProperties.toString());
        dockerProperties.store(new FileOutputStream(dockerPropertiesFile), "");
    }
}
