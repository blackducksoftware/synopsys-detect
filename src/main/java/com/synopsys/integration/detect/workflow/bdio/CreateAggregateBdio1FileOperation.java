/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;

//TODO: this may not need to exist anymore and can just use the standard write bdio operations.
public class CreateAggregateBdio1FileOperation {
    private final SimpleBdioFactory simpleBdioFactory;
    private final DetectBdioWriter detectBdioWriter;

    public CreateAggregateBdio1FileOperation(final SimpleBdioFactory simpleBdioFactory, final DetectBdioWriter detectBdioWriter) {
        this.simpleBdioFactory = simpleBdioFactory;
        this.detectBdioWriter = detectBdioWriter;
    }

    public void writeAggregateBdio1File(AggregateCodeLocation aggregateCodeLocation)
        throws DetectUserFriendlyException {
        SimpleBdioDocument aggregateBdioDocument = simpleBdioFactory.createSimpleBdioDocument(aggregateCodeLocation.getCodeLocationName(), aggregateCodeLocation.getProjectNameVersion().getName(),
            aggregateCodeLocation.getProjectNameVersion().getVersion(), aggregateCodeLocation.getProjectExternalId(), aggregateCodeLocation.getAggregateDependencyGraph());
        detectBdioWriter.writeBdioFile(aggregateCodeLocation.getAggregateFile(), aggregateBdioDocument);
    }
}
