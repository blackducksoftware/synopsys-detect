/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.Bdio;
import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.bdio2.model.Project;
import com.blackducksoftware.common.value.Product;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.blackduck.bdio2.model.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Writer;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class CreateAggregateBdio2FileOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bdio2Factory bdio2Factory;
    private final DetectInfo detectInfo;

    public CreateAggregateBdio2FileOperation(final Bdio2Factory bdio2Factory, DetectInfo detectInfo) {
        this.bdio2Factory = bdio2Factory;
        this.detectInfo = detectInfo;
    }

    public void writeAggregateBdio2File(AggregateCodeLocation aggregateCodeLocation)
        throws DetectUserFriendlyException {

        String detectVersion = detectInfo.getDetectVersion();
        SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);

        BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(aggregateCodeLocation.getCodeLocationName(), ZonedDateTime.now(), new Product.Builder().name(detectCreator.getIdentifier()).build());
        bdioMetadata.scanType(Bdio.ScanType.PACKAGE_MANAGER);

        Project project = bdio2Factory.createProject(aggregateCodeLocation.getProjectExternalId(), aggregateCodeLocation.getProjectNameVersion().getName(), aggregateCodeLocation.getProjectNameVersion().getVersion(), true);
        Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, project, aggregateCodeLocation.getAggregateDependencyGraph());

        Bdio2Writer bdio2Writer = new Bdio2Writer();
        try {
            OutputStream outputStream = new FileOutputStream(aggregateCodeLocation.getAggregateFile());
            bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
            logger.debug(String.format("BDIO Generated: %s", aggregateCodeLocation.getAggregateFile().getAbsolutePath()));
        } catch (IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}