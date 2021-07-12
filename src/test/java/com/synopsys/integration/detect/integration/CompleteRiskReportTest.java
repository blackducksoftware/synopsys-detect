/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;

@Tag("integration")
public class CompleteRiskReportTest {
    @Test
    public void testRiskReportWithoutPath() throws Exception {
        testRiskReportIsPopulated(false);
    }

    @Test
    public void testRiskReportWithPath() throws Exception {
        testRiskReportIsPopulated(true);
    }

    //Tests that a new project has an empty report, run detect to fill it, tests the report is filled.
    public void testRiskReportIsPopulated(boolean includePath) throws Exception {
        Path tempReportDirectoryPath = Files.createTempDirectory("junit_report");
        File reportDirectory;
        if (includePath) {
            reportDirectory = tempReportDirectoryPath.toFile();
        } else {
            reportDirectory = new File(".");
        }

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        ReportService reportService = blackDuckTestConnection.createReportService();

        BlackDuckAssertions blackDuckAssertions = blackDuckTestConnection.projectVersionAssertions("synopsys-detect-junit", "risk-report");
        ProjectVersionWrapper projectVersionWrapper = blackDuckAssertions.emptyOnBlackDuck();

        List<File> pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(0, pdfFiles.size());
        File riskReportPdf = reportService.createReportPdfFile(reportDirectory, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView());
        pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(1, pdfFiles.size());
        long initialFileLength = pdfFiles.get(0).length();
        assertTrue(initialFileLength > 0);
        FileUtils.deleteQuietly(pdfFiles.get(0));
        pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(0, pdfFiles.size());

        DetectCommandBuilder detectCommandBuilder = new DetectCommandBuilder();
        detectCommandBuilder.connectToBlackDuck(blackDuckTestConnection);
        detectCommandBuilder.projectNameVersion(blackDuckAssertions.getProjectNameVersion());
        detectCommandBuilder.property(DetectProperties.DETECT_RISK_REPORT_PDF, "true");
        detectCommandBuilder.property(DetectProperties.DETECT_TIMEOUT, "1200");
        if (includePath) {
            detectCommandBuilder.property(DetectProperties.DETECT_RISK_REPORT_PDF_PATH, reportDirectory.toString());
        }
        detectCommandBuilder.tools(DetectTool.DETECTOR);

        Application.setShouldExit(false);
        Application.main(detectCommandBuilder.buildArguments());

        pdfFiles = getPdfFiles(reportDirectory);
        assertEquals(1, pdfFiles.size());
        long postLength = pdfFiles.get(0).length();
        assertTrue(postLength > initialFileLength);
        FileUtils.deleteQuietly(riskReportPdf);
    }

    private List<File> getPdfFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.stream(files)
                       .filter(file -> file.getName().endsWith(".pdf"))
                       .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
