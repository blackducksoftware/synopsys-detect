/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.util.NameVersion;

public class CreateBdio1FilesOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectBdioWriter detectBdioWriter;
    private final SimpleBdioFactory simpleBdioFactory;

    public CreateBdio1FilesOperation(DetectBdioWriter detectBdioWriter, SimpleBdioFactory simpleBdioFactory) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public List<UploadTarget> createBdioFiles(BdioCodeLocationResult bdioCodeLocationResult, File outputDirectory, NameVersion projectNameVersion)
        throws DetectUserFriendlyException {
        logger.debug("Creating BDIO files from code locations.");
        List<UploadTarget> uploadTargets = new ArrayList<>();
        for (BdioCodeLocation bdioCodeLocation : bdioCodeLocationResult.getBdioCodeLocations()) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            File bdioOutputFile = new File(outputDirectory, bdioCodeLocation.getBdioName() + ".jsonld");
            SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), externalId, dependencyGraph);

            detectBdioWriter.writeBdioFile(bdioOutputFile, simpleBdioDocument);
            uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdioOutputFile));
        }

        return uploadTargets;
    }
}
