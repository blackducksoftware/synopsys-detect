/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.bdio2.model.Project;
import com.blackducksoftware.common.value.ProductList;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Writer;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.util.NameVersion;

public class AggregateBdioWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bdio2Factory bdio2Factory;
    private final SimpleBdioFactory simpleBdioFactory;
    private final DetectBdioWriter detectBdioWriter;

    public AggregateBdioWriter(final Bdio2Factory bdio2Factory, final SimpleBdioFactory simpleBdioFactory, final DetectBdioWriter detectBdioWriter) {
        this.bdio2Factory = bdio2Factory;
        this.simpleBdioFactory = simpleBdioFactory;
        this.detectBdioWriter = detectBdioWriter;
    }

    public void writeAggregateBdioFile(final File aggregateFile, String codeLocationName, NameVersion projectNameVersion, ExternalId projectExternalId, DependencyGraph aggregateDependencyGraph, boolean useBdio2)
        throws DetectUserFriendlyException {
        if (useBdio2) {
            writeAggregateBdio2File(aggregateFile, codeLocationName, projectNameVersion, projectExternalId, aggregateDependencyGraph);
        } else {
            writeAggregateBdio1File(aggregateFile, codeLocationName, projectNameVersion, projectExternalId, aggregateDependencyGraph);
        }
    }

    private void writeAggregateBdio1File(final File aggregateFile, String codeLocationName, NameVersion projectNameVersion, ExternalId projectExternalId, DependencyGraph aggregateDependencyGraph)
        throws DetectUserFriendlyException {
        final SimpleBdioDocument aggregateBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), projectExternalId, aggregateDependencyGraph);

        detectBdioWriter.writeBdioFile(aggregateFile, aggregateBdioDocument);
    }

    private void writeAggregateBdio2File(final File aggregateFile, String codeLocationName, NameVersion projectNameVersion, ExternalId projectExternalId, DependencyGraph aggregateDependencyGraph)
        throws DetectUserFriendlyException {

        final BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(codeLocationName, ZonedDateTime.now(), new ProductList.Builder());
        final Project project = bdio2Factory.createProject(projectExternalId, projectNameVersion.getName(), projectNameVersion.getVersion());
        final Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, project, aggregateDependencyGraph);

        final Bdio2Writer bdio2Writer = new Bdio2Writer();
        try {
            final OutputStream outputStream = new FileOutputStream(aggregateFile);
            bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
            logger.debug(String.format("BDIO Generated: %s", aggregateFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
