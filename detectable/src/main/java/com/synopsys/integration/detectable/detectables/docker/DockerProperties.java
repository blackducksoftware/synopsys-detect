/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

public class DockerProperties {
    private final DockerDetectableOptions dockerDetectableOptions;

    public DockerProperties(final DockerDetectableOptions dockerDetectableOptions) {
        this.dockerDetectableOptions = dockerDetectableOptions;
    }

    public void populatePropertiesFile(final File dockerPropertiesFile, final File outputDirectory) throws IOException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.synopsys", dockerDetectableOptions.getDockerInspectorLoggingLevel());
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("phone.home", "false");
        dockerProperties.setProperty("caller.name", "Detect");

        final Map<String, String> additionalDockerProperties = dockerDetectableOptions.getAdditionalDockerProperties();
        dockerProperties.putAll(additionalDockerProperties);

        dockerProperties.store(new FileOutputStream(dockerPropertiesFile), "");
    }
}
