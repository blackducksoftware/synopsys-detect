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
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.testutils.ObjectPrinter;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction.ExtractionResultType;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportWriter;

public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);

    public void startedExtraction(final ReportWriter writer, final BomTool bomTool, final ExtractionId extractionId) {
        writer.writeHeader();
        final String bomToolName = bomTool.getBomToolGroupType() + " - " + bomTool.getName();
        writer.writeLine("Starting extraction: " + bomToolName);
        writer.writeLine("Identifier: " + extractionId.toUniqueString());
        ObjectPrinter.printObjectPrivate(writer, bomTool);
        writer.writeLine(ReportConstants.SEPERATOR);
    }

    public void endedExtraction(final ReportWriter writer, final Extraction extraction) {
        writer.writeHeader();
        writer.writeLine("Finished extraction: " + extraction.result.toString());
        writer.writeLine("Code locations found: " + extraction.codeLocations.size());
        if (extraction.result == ExtractionResultType.EXCEPTION) {
            writer.writeLine("Exception:" + extraction.error.getMessage());
            logger.error("Exception:" + extraction.error);
        } else if (extraction.result == ExtractionResultType.FAILURE) {
            writer.writeLine(extraction.description);
        }
        writer.writeHeader();
    }

}
