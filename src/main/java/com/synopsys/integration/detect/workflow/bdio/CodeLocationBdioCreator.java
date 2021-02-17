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
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.bdio2.model.Project;
import com.blackducksoftware.common.value.Product;
import com.blackducksoftware.common.value.ProductList;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Writer;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationBdioCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectBdioWriter detectBdioWriter;
    private final SimpleBdioFactory simpleBdioFactory;
    private final Bdio2Factory bdio2Factory;
    private final DetectInfo detectInfo;

    public CodeLocationBdioCreator(final DetectBdioWriter detectBdioWriter, final SimpleBdioFactory simpleBdioFactory, final Bdio2Factory bdio2Factory, final DetectInfo detectInfo) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
        this.bdio2Factory = bdio2Factory;
        this.detectInfo = detectInfo;
    }

    public List<UploadTarget> createBdioFiles(final File bdioOutput, final List<BdioCodeLocation> bdioCodeLocations, final NameVersion projectNameVersion, boolean bdio2) throws DetectUserFriendlyException {
        if (bdio2) {
            return createBdio2Files(bdioOutput, bdioCodeLocations, projectNameVersion);
        } else {
            return createBdio1Files(bdioOutput, bdioCodeLocations, projectNameVersion);
        }
    }

    private List<UploadTarget> createBdio1Files(final File bdioOutput, final List<BdioCodeLocation> bdioCodeLocations, final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<UploadTarget> uploadTargets = new ArrayList<>();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            final String codeLocationName = bdioCodeLocation.getCodeLocationName();
            final ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            final DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            final File bdioOutputFile = new File(bdioOutput, bdioCodeLocation.getBdioName() + ".jsonld");
            final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), externalId, dependencyGraph);

            detectBdioWriter.writeBdioFile(bdioOutputFile, simpleBdioDocument);
            uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdioOutputFile));
        }

        return uploadTargets;
    }

    private List<UploadTarget> createBdio2Files(final File bdioOutput, final List<BdioCodeLocation> bdioCodeLocations, final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<UploadTarget> uploadTargets = new ArrayList<>();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            final String codeLocationName = bdioCodeLocation.getCodeLocationName();
            final ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            final DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            // Bdio 2
            final ProductList.Builder productListBuilder = new ProductList.Builder();
            final String detectVersion = detectInfo.getDetectVersion();
            final SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);
            final Product product = new Product.Builder().name(detectCreator.getIdentifier()).build();
            productListBuilder.addProduct(product);

            final BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(codeLocationName, ZonedDateTime.now(), productListBuilder);
            final Project bdio2Project = bdio2Factory.createProject(externalId, projectNameVersion.getName(), projectNameVersion.getVersion());
            final Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, bdio2Project, dependencyGraph);

            final Bdio2Writer bdio2Writer = new Bdio2Writer();
            final File bdio2OutputFile = new File(bdioOutput, bdioCodeLocation.getBdioName() + ".bdio");

            try {
                final OutputStream outputStream = new FileOutputStream(bdio2OutputFile);
                bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
                logger.debug(String.format("BDIO Generated: %s", bdio2OutputFile.getAbsolutePath()));

                uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdio2OutputFile));
            } catch (final IOException e) {
                throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }

        return uploadTargets;
    }
}
