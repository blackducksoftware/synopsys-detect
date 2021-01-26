/**
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
package com.synopsys.integration.detect.workflow.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class OverviewSummaryReporter {
    public void writeReport(final ReportWriter writer, final DetectorEvaluationTree rootEvaluationTree) {
        writeSummaries(writer, rootEvaluationTree.asFlatList());
    }

    private void writeSummaries(final ReportWriter writer, final List<DetectorEvaluationTree> detectorEvaluationTrees) {
        writer.writeSeparator();
        for (final DetectorEvaluationTree detectorEvaluationTree : detectorEvaluationTrees) {
            for (final DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
                if (detectorEvaluation.isSearchable() && detectorEvaluation.isApplicable()) {
                    writer.writeLine("DIRECTORY: " + detectorEvaluationTree.getDirectory());
                    writer.writeLine("DETECTOR: " + detectorEvaluation.getDetectorRule().getDescriptiveName());
                    writer.writeLine("\tEXTRACTABLE: " + detectorEvaluation.getExtractabilityMessage());
                    writer.writeLine("\tEXTRACTED: " + detectorEvaluation.wasExtractionSuccessful());
                    if (detectorEvaluation.getExtraction() != null && StringUtils.isNotBlank(detectorEvaluation.getExtraction().getDescription())) {
                        writer.writeLine("\tEXTRACTION: " + detectorEvaluation.getExtraction().getDescription());

                    }
                    final Map<String, String> data = new HashMap<>();
                    ObjectPrinter.populateObjectPrivate(null, detectorEvaluation.getDetectable(), data);
                    data.forEach((key, value) -> writer.writeLine("\t" + key + ": " + value));
                }
            }
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

}
