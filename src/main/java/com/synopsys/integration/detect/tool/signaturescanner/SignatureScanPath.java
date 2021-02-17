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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

public class SignatureScanPath {
    private File targetPath;
    private String targetCanonicalPath;
    private final Set<String> exclusions = new HashSet<>();

    public File getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(final File targetPath) {
        this.targetPath = targetPath;
        this.targetCanonicalPath = DetectFileUtils.tryGetCanonicalPath(targetPath);
    }

    public Set<String> getExclusions() {
        return exclusions;
    }

    public String getTargetCanonicalPath() {
        return targetCanonicalPath;
    }
}
