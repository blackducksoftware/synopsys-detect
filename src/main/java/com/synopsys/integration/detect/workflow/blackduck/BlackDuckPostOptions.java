/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.nio.file.Path;
import java.util.List;

import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;

public class BlackDuckPostOptions {
    private final boolean waitForResults;

    private final boolean generateRiskReport;
    private final boolean generateNoticesReport;
    private final Path riskReportPdfPath;
    private final Path noticesReportPath;
    private final List<PolicyRuleSeverityType> severitiesToFailPolicyCheck;

    public BlackDuckPostOptions(final boolean waitForResults, final boolean generateRiskReport, final boolean generateNoticesReport, final Path riskReportPdfPath, final Path noticesReportPath,
        final List<PolicyRuleSeverityType> severitiesToFailPolicyCheck) {
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

    public Path getRiskReportPdfPath() {
        return riskReportPdfPath;
    }

    public Path getNoticesReportPath() {
        return noticesReportPath;
    }

    public List<PolicyRuleSeverityType> getSeveritiesToFailPolicyCheck() {
        return severitiesToFailPolicyCheck;
    }

}
