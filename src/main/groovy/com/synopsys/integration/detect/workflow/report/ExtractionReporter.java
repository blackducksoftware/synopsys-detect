/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.report;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.synopsys.integration.detect.tool.detector.impl.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class ExtractionReporter {
    private Integer extractionCount = 0;

    public void setExtractionCount(final Integer count) {
        extractionCount = count;
    }

    public void extractionStarted(final ReportWriter writer, final DetectorEvaluation detectorEvaluation) {
        final DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) detectorEvaluation.getExtractionEnvironment();
        final Integer i = detectExtractionEnvironment.getExtractionId().getId();
        final String progress = Integer.toString((int) Math.floor((i * 100.0f) / extractionCount));
        writer.writeLine(String.format("Extracting %d of %d (%s%%)", i + 1, extractionCount, progress));
        writer.writeLine(ReportConstants.SEPERATOR);

        writer.writeLine("Starting extraction: " + detectorEvaluation.getDetectorRule().getDetectorType() + " - " + detectorEvaluation.getDetectorRule().getName());
        writer.writeLine("Identifier: " + detectExtractionEnvironment.getExtractionId().toUniqueString());
        ObjectPrinter.printObjectPrivate(writer, detectorEvaluation.getDetectable());
        writer.writeLine(ReportConstants.SEPERATOR);
    }

    public void extractionEnded(final ReportWriter writer, final DetectorEvaluation detectorEvaluation) {
        writer.writeLine(ReportConstants.SEPERATOR);
        writer.writeLine("Finished extraction: " + detectorEvaluation.getExtraction().getResult().toString());
        writer.writeLine("Code locations found: " + detectorEvaluation.getExtraction().getCodeLocations().size());
        if (detectorEvaluation.getExtraction().getResult() == Extraction.ExtractionResultType.EXCEPTION) { //TODO add back in trace when in debug.
            writer.writeLine("Exception: " + ExceptionUtil.oneSentenceDescription(detectorEvaluation.getExtraction().getError()));
        } else if (detectorEvaluation.getExtraction().getResult() == Extraction.ExtractionResultType.FAILURE) {
            writer.writeLine(detectorEvaluation.getExtraction().getDescription());
        }
        writer.writeLine(ReportConstants.SEPERATOR);
    }
}
