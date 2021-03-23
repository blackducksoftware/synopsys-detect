/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
import com.synopsys.integration.blackduck.bdio2.model.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Writer;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
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

    public CodeLocationBdioCreator(DetectBdioWriter detectBdioWriter, SimpleBdioFactory simpleBdioFactory, Bdio2Factory bdio2Factory, DetectInfo detectInfo) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
        this.bdio2Factory = bdio2Factory;
        this.detectInfo = detectInfo;
    }

    public List<UploadTarget> createBdioFiles(File bdioOutput, List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion, boolean bdio2) throws DetectUserFriendlyException {
        if (bdio2) {
            return createBdio2Files(bdioOutput, bdioCodeLocations, projectNameVersion);
        } else {
            return createBdio1Files(bdioOutput, bdioCodeLocations, projectNameVersion);
        }
    }

    private List<UploadTarget> createBdio1Files(File bdioOutput, List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        List<UploadTarget> uploadTargets = new ArrayList<>();
        for (BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            File bdioOutputFile = new File(bdioOutput, bdioCodeLocation.getBdioName() + ".jsonld");
            SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), externalId, dependencyGraph);

            detectBdioWriter.writeBdioFile(bdioOutputFile, simpleBdioDocument);
            uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdioOutputFile));
        }

        return uploadTargets;
    }

    private List<UploadTarget> createBdio2Files(File bdioOutput, List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        List<UploadTarget> uploadTargets = new ArrayList<>();
        for (BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            // Bdio 2
            ProductList.Builder productListBuilder = new ProductList.Builder();
            String detectVersion = detectInfo.getDetectVersion();
            SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);
            Product product = new Product.Builder().name(detectCreator.getIdentifier()).build();
            productListBuilder.addProduct(product);

            BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(codeLocationName, ZonedDateTime.now(), productListBuilder);
            Project bdio2Project = bdio2Factory.createProject(externalId, projectNameVersion.getName(), projectNameVersion.getVersion());
            Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, bdio2Project, dependencyGraph);

            Bdio2Writer bdio2Writer = new Bdio2Writer();
            File bdio2OutputFile = new File(bdioOutput, bdioCodeLocation.getBdioName() + ".bdio");

            try {
                OutputStream outputStream = new FileOutputStream(bdio2OutputFile);
                bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
                logger.debug(String.format("BDIO Generated: %s", bdio2OutputFile.getAbsolutePath()));

                uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdio2OutputFile));
            } catch (IOException e) {
                throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }

        return uploadTargets;
    }
}
