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
package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.SignatureScannerService;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class OnlineBlackDuckSignatureScanner extends BlackDuckSignatureScanner {
    private final CodeLocationCreationService codeLocationCreationService;
    private final BlackDuckServerConfig hubServerConfig;

    public OnlineBlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder,
        final CodeLocationNameManager codeLocationNameManager, final BlackDuckSignatureScannerOptions signatureScannerOptions, final EventSystem eventSystem, final ScanBatchRunner scanBatchRunner, final CodeLocationCreationService codeLocationCreationService,
        final BlackDuckServerConfig hubServerConfig) {
        super(directoryManager, detectFileFinder, codeLocationNameManager, signatureScannerOptions, eventSystem, scanBatchRunner);
        this.codeLocationCreationService = codeLocationCreationService;
        this.hubServerConfig = hubServerConfig;
    }

    public CodeLocationCreationData<ScanBatchOutput> performOnlineScan(NameVersion projectNameVersion, File installDirectory, File dockerTarFile) throws InterruptedException, IntegrationException, DetectUserFriendlyException, IOException {
        NotificationTaskRange notificationTaskRange = codeLocationCreationService.calculateCodeLocationRange();
        ScanBatchOutput scanBatchOutput = performScanActions(projectNameVersion, installDirectory, dockerTarFile);
        CodeLocationCreationData<ScanBatchOutput> creationData = new CodeLocationCreationData<>(notificationTaskRange, scanBatchOutput);
        return creationData;
    }

    @Override
    protected ScanBatch createScanBatch(NameVersion projectNameVersion, File installDirectory, List<SignatureScanPath> signatureScanPaths, File dockerTarFile) {
        final ScanBatchBuilder scanJobBuilder = createDefaultScanBatchBuilder(projectNameVersion, installDirectory, signatureScanPaths, dockerTarFile);
        scanJobBuilder.fromBlackDuckServerConfig(hubServerConfig);
        return scanJobBuilder.build();
    }

}
