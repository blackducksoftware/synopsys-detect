/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationBdioCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectBdioWriter detectBdioWriter;
    private SimpleBdioFactory simpleBdioFactory;

    public CodeLocationBdioCreator(final DetectBdioWriter detectBdioWriter, final SimpleBdioFactory simpleBdioFactory) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public List<UploadTarget> createBdioFiles(File bdioOutput, final List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<UploadTarget> uploadTargets = new ArrayList<>();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), externalId, dependencyGraph);

            final File outputFile = new File(bdioOutput, bdioCodeLocation.getBdioName());
            detectBdioWriter.writeBdioFile(outputFile, simpleBdioDocument);
            uploadTargets.add(UploadTarget.createDefault(codeLocationName, outputFile));
        }

        return uploadTargets;
    }
}
