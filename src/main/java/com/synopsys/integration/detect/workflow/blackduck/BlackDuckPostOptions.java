package com.synopsys.integration.detect.workflow.blackduck;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;

public class BlackDuckPostOptions {
    private final boolean waitForResults;

    private final boolean generateRiskReport;
    private final boolean generateNoticesReport;
    private final @Nullable Path riskReportPdfPath;
    private final @Nullable Path noticesReportPath;
    private final List<PolicyRuleSeverityType> severitiesToFailPolicyCheck;

    public BlackDuckPostOptions(
        boolean waitForResults,
        boolean generateRiskReport,
        boolean generateNoticesReport,
        @Nullable Path riskReportPdfPath,
        @Nullable Path noticesReportPath,
        List<PolicyRuleSeverityType> severitiesToFailPolicyCheck
    ) {
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

    public Optional<Path> getRiskReportPdfPath() {
        return Optional.ofNullable(riskReportPdfPath);
    }

    public Optional<Path> getNoticesReportPath() {
        return Optional.ofNullable(noticesReportPath);
    }

    public List<PolicyRuleSeverityType> getSeveritiesToFailPolicyCheck() {
        return severitiesToFailPolicyCheck;
    }

}
