package com.blackducksoftware.integration.hub.detect.workflow.hub;

public class BlackduckReportOptions {

    private boolean generateRiskReport;
    private boolean generateNoticesReport;
    private String riskReportPdfPath;
    private String noticesReportPath;

    public BlackduckReportOptions(final boolean generateRiskReport, final boolean generateNoticesReport, final String riskReportPdfPath, final String noticesReportPath) {
        this.generateRiskReport = generateRiskReport;
        this.generateNoticesReport = generateNoticesReport;
        this.riskReportPdfPath = riskReportPdfPath;
        this.noticesReportPath = noticesReportPath;
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

    public String getRiskReportPdfPath() {
        return riskReportPdfPath;
    }

    public String getNoticesReportPath() {
        return noticesReportPath;
    }
}
