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

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class SearchSummaryReporter {

    public void print(final ReportWriter writer, final DetectorEvaluationTree rootEvaluation) {
        printDirectoriesInfo(writer, rootEvaluation.asFlatList());
    }

    private void printDirectoriesInfo(final ReportWriter writer, final List<DetectorEvaluationTree> trees) {
        ReporterUtils.printHeader(writer, "Search results");
        boolean printedAtLeastOne = false;
        for (final DetectorEvaluationTree tree : trees) {
            final List<DetectorEvaluation> applicable = DetectorEvaluationUtils.applicableChildren(tree);
            if (applicable.size() > 0) {
                writer.writeLine(tree.getDirectory().toString());
                writer.writeLine("\tAPPLIES: " + applicable.stream().map(it -> it.getDetectorRule().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                printedAtLeastOne = true;
            }
        }
        if (!printedAtLeastOne) {
            writer.writeLine("No detectors found.");
        }
        ReporterUtils.printFooter(writer);
    }

}
