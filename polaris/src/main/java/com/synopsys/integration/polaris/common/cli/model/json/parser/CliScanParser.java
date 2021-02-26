/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model.json.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.polaris.common.cli.model.*;
import com.synopsys.integration.polaris.common.cli.model.json.CliScanResponse;
import com.synopsys.integration.polaris.common.cli.model.json.v1.IssueSummaryV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ProjectInfoV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ScanInfoV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ToolInfoV1;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;

import java.util.function.Consumer;

public abstract class CliScanParser<T extends CliScanResponse> {
    private final Gson gson;

    public CliScanParser(Gson gson) {
        this.gson = gson;
    }

    public abstract TypeToken<T> getTypeToken();

    public abstract CliCommonResponseModel fromCliScan(JsonObject versionlessModel) throws PolarisIntegrationException;

    protected T fromJson(JsonObject jsonObject) {
        return gson.fromJson(jsonObject, getTypeToken().getType());
    }

    protected CliCommonResponseModel createResponseModel(IssueSummaryV1 issueSummary, ProjectInfoV1 projectInfo, ScanInfoV1 scanInfo) {
        final CliCommonResponseModel cliCommonResponseModel = new CliCommonResponseModel();

        populateIssueSummary(issueSummary, cliCommonResponseModel::setIssueSummary);
        populateProjectInfo(projectInfo, cliCommonResponseModel::setProjectInfo);
        populateScanInfo(scanInfo, cliCommonResponseModel::setScanInfo);
        return cliCommonResponseModel;
    }

    protected void populateScanInfo(ScanInfoV1 scanInfoV1, Consumer<CommonScanInfo> consumer) {
        CommonScanInfo commonScanInfo = new CommonScanInfo();
        commonScanInfo.setCliVersion(scanInfoV1.cliVersion);
        commonScanInfo.setIssueApiUrl(scanInfoV1.issueApiUrl);
        commonScanInfo.setScanTime(scanInfoV1.scanTime);

        consumer.accept(commonScanInfo);
    }

    protected void populateProjectInfo(ProjectInfoV1 projectInfoV1, Consumer<CommonProjectInfo> consumer) {
        CommonProjectInfo commonProjectInfo = new CommonProjectInfo();
        commonProjectInfo.setBranchId(projectInfoV1.branchId);
        commonProjectInfo.setProjectId(projectInfoV1.projectId);
        commonProjectInfo.setRevisionId(projectInfoV1.revisionId);

        consumer.accept(commonProjectInfo);
    }

    protected void populateIssueSummary(IssueSummaryV1 issueSummaryV1, Consumer<CommonIssueSummary> consumer) {
        if (null != issueSummaryV1) {
            CommonIssueSummary commonIssueSummary = new CommonIssueSummary();
            commonIssueSummary.setIssuesBySeverity(issueSummaryV1.issuesBySeverity);
            commonIssueSummary.setSummaryUrl(issueSummaryV1.summaryUrl);
            commonIssueSummary.setTotalIssueCount(issueSummaryV1.total);

            consumer.accept(commonIssueSummary);
        }
    }

    protected CommonToolInfo createCommonToolInfo(ToolInfoV1 toolInfoV1) {
        CommonToolInfo commonToolInfo = new CommonToolInfo();
        commonToolInfo.setJobId(toolInfoV1.jobId);
        commonToolInfo.setJobStatus(toolInfoV1.jobStatus);
        commonToolInfo.setJobStatusUrl(toolInfoV1.jobStatusUrl);
        commonToolInfo.setToolVersion(toolInfoV1.toolVersion);
        return commonToolInfo;
    }

}
