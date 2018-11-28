/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.EncryptionException;
import com.synopsys.integration.util.NameVersion;

public class OnlineBlackDuckSignatureScanner extends BlackDuckSignatureScanner {

    private final HubServerConfig hubServerConfig;

    public OnlineBlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder,
        final CodeLocationNameManager codeLocationNameManager, final BlackDuckSignatureScannerOptions signatureScannerOptions, final EventSystem eventSystem, final ScanJobManager scanJobManager,
        final HubServerConfig hubServerConfig) {
        super(directoryManager, detectFileFinder, codeLocationNameManager, signatureScannerOptions, eventSystem, scanJobManager);
        this.hubServerConfig = hubServerConfig;
    }

    @Override
    protected ScanJob createScanJob(NameVersion projectNameVersion, File installDirectory, List<SignatureScanPath> signatureScanPaths, File dockerTarFile) {
        final ScanJobBuilder scanJobBuilder = createDefaultScanJobBuilder(projectNameVersion, installDirectory, signatureScanPaths, dockerTarFile);
        try {
            scanJobBuilder.fromHubServerConfig(hubServerConfig);
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
        return scanJobBuilder.build();
    }
}
