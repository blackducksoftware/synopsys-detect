/*
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;

public class LocalPipInspectorResolver implements PipInspectorResolver {
    public static final String INSPECTOR_NAME = "pip-inspector.py";

    private final DirectoryManager directoryManager;

    private File resolvedInspector = null;
    private boolean hasResolvedInspector = false;

    public LocalPipInspectorResolver(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    public File resolvePipInspector() throws DetectableException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInspector = installInspector();
            }
            return resolvedInspector;
        } catch (Exception e) {
            throw new DetectableException(e);
        }
    }

    private File installInspector() throws IOException {
        String inspectorScriptContents;
        try (InputStream inspectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME))) {
            inspectorScriptContents = IOUtils.toString(inspectorFileStream, StandardCharsets.UTF_8);
        }
        File inspectorScript = directoryManager.getSharedFile("pip", INSPECTOR_NAME); //Moved the file getting so the pip folder would not be created every time. -jp
        FileUtils.write(inspectorScript, inspectorScriptContents, StandardCharsets.UTF_8);
        return inspectorScript;
    }

}
