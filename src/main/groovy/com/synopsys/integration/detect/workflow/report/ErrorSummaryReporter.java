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
package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.function.Function;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class ErrorSummaryReporter {

    public void writeSummary(final ReportWriter writer, final DetectorEvaluationTree rootEvaluationTree) {
        writeSummaries(writer, rootEvaluationTree.asFlatList());
    }

    private void writeSummaries(final ReportWriter writer, final List<DetectorEvaluationTree> trees) {
        boolean printedOne = false;
        for (final DetectorEvaluationTree tree : trees) {
            final List<DetectorEvaluation> excepted = DetectorEvaluationUtils.filteredChildren(tree, DetectorEvaluation::wasExtractionException);
            final List<DetectorEvaluation> failed = DetectorEvaluationUtils.filteredChildren(tree, DetectorEvaluation::wasExtractionFailure);
            final List<DetectorEvaluation> notExtractable = DetectorEvaluationUtils.filteredChildren(tree, (evaluation) -> evaluation.isApplicable() && !evaluation.isExtractable());
            if (excepted.size() > 0 || failed.size() > 0 || notExtractable.size() > 0) {
                if (!printedOne) {
                    printedOne = true;
                    ReporterUtils.printHeader(writer, "Detector Issue Summary");
                }
                writer.writeLine(tree.getDirectory().toString());
                final String spacer = "\t\t";
                writeEvaluationsIfNotEmpty(writer, "\tNot Extractable: ", spacer, notExtractable, DetectorEvaluation::getExtractabilityMessage);
                writeEvaluationsIfNotEmpty(writer, "\tFailure: ", spacer, failed, detectorEvaluation -> detectorEvaluation.getExtraction().getDescription());
                writeEvaluationsIfNotEmpty(writer, "\tException: ", spacer, excepted, detectorEvaluation -> ExceptionUtil.oneSentenceDescription(detectorEvaluation.getExtraction().getError()));
            }
        }
        if (printedOne) {
            ReporterUtils.printFooter(writer);
        }
    }



    private void writeEvaluationsIfNotEmpty(final ReportWriter writer, final String prefix, final String spacer, final List<DetectorEvaluation> evaluations, final Function<DetectorEvaluation, String> reason) {
        if (evaluations.size() > 0) {
            evaluations.stream().forEach(evaluation -> {
                writer.writeLine(prefix + evaluation.getDetectorRule());
                writer.writeLine(spacer + reason.apply(evaluation));
            });
        }
    }

}
