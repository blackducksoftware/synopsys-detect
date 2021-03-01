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
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;

@Tag("integration")
public class CompleteRiskReportTest extends BlackDuckIntegrationTest {
    @Test
    public void testRiskReportWithoutPath() throws Exception {
        testRiskReportIsPopulated(false);
    }

    @Test
    public void testRiskReportWithPath() throws Exception {
        testRiskReportIsPopulated(true);
    }

    public void testRiskReportIsPopulated(boolean includePath) throws Exception {
        Path tempReportDirectoryPath = Files.createTempDirectory("junit_report");
        File reportDirectory;
        if (includePath) {
            reportDirectory = tempReportDirectoryPath.toFile();
        } else {
            reportDirectory = new File(".");
        }

        final String projectName = "synopsys-detect-junit";
        final String projectVersionName = "risk-report";
        ProjectVersionWrapper projectVersionWrapper = assertProjectVersionReady(projectName, projectVersionName);
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

        List<String> detectArgs = getInitialArgs(projectName, projectVersionName);
        detectArgs.add("--detect.risk.report.pdf=true");
        if (includePath) {
            detectArgs.add("--detect.risk.report.pdf.path=" + reportDirectory.toString());
        }

        detectArgs.add("--detect.tools=DETECTOR");
        detectArgs.forEach(System.out::println);
        Application.main(detectArgs.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

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
