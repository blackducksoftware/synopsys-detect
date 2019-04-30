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
package com.synopsys.integration.detect.workflow.blackduck;

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;

import java.util.List;

public class BlackduckPostOptions {
    private boolean waitForResults;

    private boolean generateRiskReport;
    private boolean generateNoticesReport;
    private String riskReportPdfPath;
    private String noticesReportPath;
    private List<PolicySeverityType> severitiesToFailPolicyCheck;

    public BlackduckPostOptions(boolean waitForResults, boolean generateRiskReport, boolean generateNoticesReport, String riskReportPdfPath, String noticesReportPath, List<PolicySeverityType> severitiesToFailPolicyCheck) {
        this.waitForResults = waitForResults;
        this.generateRiskReport = generateRiskReport;
        this.generateNoticesReport = generateNoticesReport;
        this.riskReportPdfPath = riskReportPdfPath;
        this.noticesReportPath = noticesReportPath;
        this.severitiesToFailPolicyCheck = severitiesToFailPolicyCheck;
    }

    public boolean shouldWaitForResults() {
        return waitForResults || shouldGenerateAnyReport() || shouldPerformPolicyCheck();
    }

    public boolean shouldGenerateRiskReport() {
        return generateRiskReport;
    }

    public boolean shouldGenerateNoticesReport() {
        return generateNoticesReport;
    }

    public boolean shouldGenerateAnyReport() {
        return shouldGenerateNoticesReport() || shouldGenerateRiskReport();
    }

    public boolean shouldPerformPolicyCheck() {
        return severitiesToFailPolicyCheck.size() > 0;
    }

    public String getRiskReportPdfPath() {
        return riskReportPdfPath;
    }

    public String getNoticesReportPath() {
        return noticesReportPath;
    }

    public List<PolicySeverityType> getSeveritiesToFailPolicyCheck() {
        return severitiesToFailPolicyCheck;
    }

}
