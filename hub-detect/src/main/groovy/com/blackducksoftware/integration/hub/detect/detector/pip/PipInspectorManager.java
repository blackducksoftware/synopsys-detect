/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.detector.pip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileUtils;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class PipInspectorManager {
    public static final String INSPECTOR_NAME = "pip-inspector.py";

    private final DirectoryManager directoryManager;

    private File resolvedInspector = null;
    private boolean hasResolvedInspector = false;

    public PipInspectorManager(final DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public File findPipInspector(final DetectorEnvironment environment) throws DetectorException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInspector = installInspector();
            }
            return resolvedInspector;
        } catch (final Exception e) {
            throw new DetectorException(e);
        }
    }

    private File installInspector() throws IOException {
        final InputStream insptectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME));
        final String inpsectorScriptContents = IOUtils.toString(insptectorFileStream, StandardCharsets.UTF_8);
        final File inspectorScript = directoryManager.getSharedFile("pip", INSPECTOR_NAME);
        return DetectFileUtils.writeToFile(inspectorScript, inpsectorScriptContents);
    }

}
