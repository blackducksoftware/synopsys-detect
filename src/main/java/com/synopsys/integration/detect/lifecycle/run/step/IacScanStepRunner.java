package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.bdio2.Bdio;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.iac.IacScanCodeLocationData;
import com.synopsys.integration.detect.tool.iac.IacScanReport;
import com.synopsys.integration.detect.workflow.bdio.AggregateCodeLocation;
import com.synopsys.integration.detectable.util.ExternalIdCreator;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class IacScanStepRunner {
    private static final String SCAN_CREATOR = "IaC";

    private final OperationRunner operationRunner;
    private final IntegrationEscapeUtil integrationEscapeUtil; //TODO- IntegrationEscapeUtil's methods should be static

    public IacScanStepRunner(
        OperationRunner operationRunner
    ) {
        this.operationRunner = operationRunner;
        this.integrationEscapeUtil = new IntegrationEscapeUtil();
    }

    public IacScanCodeLocationData runIacScanOnline(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws OperationException, IntegrationException, InterruptedException {
        List<File> iacScanTargets = operationRunner.calculateIacScanScanTargets();

        File iacScanExe;
        Optional<File> localIacScan = operationRunner.calculateUserProvidedIacScanPath();
        if (localIacScan.isPresent()) {
            iacScanExe = localIacScan.get();
            validateIacScan(iacScanExe);
        } else {
            iacScanExe = operationRunner.resolveIacScanOnline(blackDuckRunData);
        }

        List<IacScanReport> iacScanReports = new LinkedList<>();
        int count = 0;
        for (File scanTarget : iacScanTargets) {
            IacScanReport iacScanReport = performOnlineScan(projectNameVersion, blackDuckRunData, iacScanExe, scanTarget, count++);
            iacScanReports.add(iacScanReport);
        }
        operationRunner.publishIacScanReport(iacScanReports);

        Set<String> codeLocationNames = iacScanReports.stream()
            .map(IacScanReport::getCodeLocationName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        return new IacScanCodeLocationData(codeLocationNames);
    }

    public void runIacScanOffline() throws OperationException, IntegrationException {
        List<File> iacScanTargets = operationRunner.calculateIacScanScanTargets();
        File iacScanExe = operationRunner.calculateUserProvidedIacScanPath()
            .orElseThrow(() -> new IntegrationException("Was not able to install or locate IacScan.  Must either connect to a Black Duck or provide a path to a local IacScan."));
        validateIacScan(iacScanExe);
        List<IacScanReport> iacScanReports = new LinkedList<>();
        int count = 0;
        for (File scanTarget : iacScanTargets) {
            IacScanReport iacScanReport = performOfflineScan(scanTarget, iacScanExe, count++);
            iacScanReports.add(iacScanReport);
        }
        operationRunner.publishIacScanReport(iacScanReports);
    }

    private void validateIacScan(File iacScanExe) throws IntegrationException {
        if (!iacScanExe.exists()) {
            throw new IntegrationException(String.format("Provided Iac Scanner %s does not exist.", iacScanExe.getAbsolutePath()));
        }
    }

    public IacScanReport performOnlineScan(
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        File iacScanExe,
        File scanTarget,
        int count
    ) throws InterruptedException {
        try {
            File resultsFile = operationRunner.performIacScanScan(scanTarget, iacScanExe, count);
            String codeLocationName = operationRunner.createIacScanCodeLocationName(scanTarget, projectNameVersion);
            String scanId = initiateScan(projectNameVersion, blackDuckRunData.getBlackDuckServicesFactory().createBdio2FileUploadService(), codeLocationName);
            operationRunner.uploadIacScanResults(blackDuckRunData, resultsFile, scanId);
            return IacScanReport.SUCCESS_ONLINE(scanTarget, codeLocationName);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            } else {
                return IacScanReport.FAILURE(scanTarget, e.getMessage());
            }
        }
    }

    public IacScanReport performOfflineScan(File scanTarget, File iacScanExe, int count) {
        try {
            operationRunner.performIacScanScan(scanTarget, iacScanExe, count);
        } catch (OperationException e) {
            return IacScanReport.FAILURE(scanTarget, e.getMessage());
        }
        return IacScanReport.SUCCESS_OFFLINE(scanTarget);
    }

    //TODO- look into extracting scan initiation to another class

    //TODO- this should only be necessary if we didn't already upload BDIO during DETECTORS phase
    public String initiateScan(NameVersion projectNameVersion, Bdio2FileUploadService bdio2FileUploadService, String codeLocationNameOverride)
        throws OperationException, IntegrationException, InterruptedException {

        ExternalId externalId = ExternalIdCreator.nameVersion(CodeLocationConverter.DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion());
        ProjectDependency projectDependency = new ProjectDependency(externalId);
        AggregateCodeLocation codeLocation = overrideAggregateCodeLocationName(
            codeLocationNameOverride,
            operationRunner.createAggregateCodeLocation(new ProjectDependencyGraph(projectDependency), projectNameVersion, GitInfo.none())
        );
        operationRunner.createAggregateBdio2File(codeLocation, Bdio.ScanType.INFRASTRUCTURE_AS_CODE);
        UploadTarget uploadTarget = UploadTarget.createDefault(codeLocation.getProjectNameVersion(), codeLocation.getCodeLocationName(), codeLocation.getAggregateFile());
        return bdio2FileUploadService.uploadFile(uploadTarget, operationRunner.calculateDetectTimeout(), false, false).getScanId();
    }

    private AggregateCodeLocation overrideAggregateCodeLocationName(String codeLocationNameOverride, AggregateCodeLocation original) {
        return new AggregateCodeLocation(
            original.getAggregateFile(),
            codeLocationNameOverride,
            original.getProjectNameVersion(),
            original.getGitInfo(),
            original.getAggregateDependencyGraph()
        );
    }
}
