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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetailedSearchSummaryReporter {
    public void print(final ReportWriter writer, final DetectorEvaluationTree rootEvaluation) {
        printDirectoriesDebug(writer, rootEvaluation.asFlatList());
    }

    private void printDirectoriesDebug(final ReportWriter writer, final List<DetectorEvaluationTree> trees) {
        for (final DetectorEvaluationTree tree : trees) {
            final List<String> toPrint = new ArrayList<>();
            toPrint.addAll(printDetails("      APPLIED: ", DetectorEvaluationUtils.applicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));
            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.notSearchableChildren(tree), DetectorEvaluation::getSearchabilityMessage));
            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.searchableButNotApplicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));

            if (toPrint.size() > 0) {
                writer.writeSeparator();
                writer.writeLine("Detailed search results for directory");
                writer.writeLine(tree.getDirectory().toString());
                writer.writeSeparator();
                toPrint.stream().sorted().forEach(writer::writeLine);
                writer.writeSeparator();
            }
        }
    }

    private List<String> printDetails(final String prefix, final List<DetectorEvaluation> details, final Function<DetectorEvaluation, String> reason) {
        final List<String> toPrint = new ArrayList<>();
        for (final DetectorEvaluation detail : details) {
            toPrint.add(prefix + detail.getDetectorRule().getDescriptiveName() + ": " + reason.apply(detail));
        }
        return toPrint;
    }
}
