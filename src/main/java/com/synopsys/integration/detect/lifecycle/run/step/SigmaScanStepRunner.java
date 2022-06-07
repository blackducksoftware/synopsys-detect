package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.bdio2.Bdio;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.sigma.SigmaReport;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detectable.util.ExternalIdCreator;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class SigmaScanStepRunner {
    private static final String SCAN_CREATOR = "sigma";

    private final OperationFactory operationFactory;

    public SigmaScanStepRunner(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public void runSigmaOnline(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws OperationException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();

        File sigmaExe;
        Optional<File> localSigma = operationFactory.calculateUserProvidedSigmaPath();
        if (localSigma.isPresent()) {
            sigmaExe = localSigma.get();
        } else {
            sigmaExe = operationFactory.resolveSigmaOnline(blackDuckRunData);
        }

        List<SigmaReport> sigmaReports = new LinkedList<>();
        int count = 0;
        for (File scanTarget : sigmaScanTargets) {
            SigmaReport sigmaReport = performOnlineScan(projectNameVersion, blackDuckRunData, sigmaExe, scanTarget, count++);
            sigmaReports.add(sigmaReport);
        }
        operationFactory.publishSigmaReport(sigmaReports);
    }

    public void runSigmaOffline() throws OperationException, IntegrationException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();
        File sigmaExe = operationFactory.calculateUserProvidedSigmaPath()
            .orElseThrow(() -> new IntegrationException("Was not able to install or locate Sigma.  Must either connect to a Black Duck or provide a path to a local Sigma."));
        List<SigmaReport> sigmaReports = new LinkedList<>();
        int count = 0;
        for (File scanTarget : sigmaScanTargets) {
            SigmaReport sigmaReport = performOfflineScan(scanTarget, sigmaExe, count++);
            sigmaReports.add(sigmaReport);
        }
        operationFactory.publishSigmaReport(sigmaReports);
    }

    public SigmaReport performOnlineScan(
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        File sigmaExe,
        File scanTarget,
        int count
    ) {
        try {
            String scanId = initiateScan(scanTarget, projectNameVersion, scanTarget, blackDuckRunData.getBlackDuckServicesFactory().createBdio2FileUploadService());
            File resultsFile = operationFactory.performSigmaScan(scanTarget, sigmaExe, count);
            operationFactory.uploadSigmaResults(blackDuckRunData, resultsFile, scanId);
            return SigmaReport.SUCCESS(scanTarget);
        } catch (Exception e) {
            return SigmaReport.FAILURE(scanTarget, e.getMessage());
        }
    }

    public SigmaReport performOfflineScan(File scanTarget, File sigmaExe, int count) {
        try {
            operationFactory.performSigmaScan(scanTarget, sigmaExe, count);
        } catch (OperationException e) {
            return SigmaReport.FAILURE(scanTarget, e.getMessage());
        }
        return SigmaReport.SUCCESS(scanTarget);
    }

    //TODO- this should only be necessary if we didn't already upload BDIO during DETECTORS phase
    public String initiateScan(File scanTarget, NameVersion projectNameVersion, File sourcePath, Bdio2FileUploadService bdio2FileUploadService)
        throws OperationException, IntegrationException, InterruptedException {
        DetectCodeLocation codeLocation = createSimpleCodeLocation(projectNameVersion, sourcePath);
        BdioCodeLocationResult bdioCodeLocationResult = operationFactory.createBdioCodeLocationsFromDetectCodeLocations(
            Collections.singletonList(codeLocation),
            projectNameVersion
        );
        UploadTarget uploadTarget = createUploadTarget(bdioCodeLocationResult, scanTarget, projectNameVersion);
        return bdio2FileUploadService.uploadFileAndGetResult(uploadTarget).getScanId();
    }

    private UploadTarget createUploadTarget(BdioCodeLocationResult bdioCodeLocationResult, File scanTarget, NameVersion projectNameVersion) throws OperationException {
        UploadTarget uploadTargetWithBadCodeLocationName = operationFactory.createBdio2Files(bdioCodeLocationResult, projectNameVersion, Bdio.ScanType.INFRASTRUCTURE_AS_CODE)
            .get(0);
        return UploadTarget.createWithMediaType(
            projectNameVersion,
            operationFactory.createSigmaCodeLocationName(scanTarget, projectNameVersion), //TODO- name doesn't ever get used...
            uploadTargetWithBadCodeLocationName.getUploadFile(),
            uploadTargetWithBadCodeLocationName.getMediaType()
        );
    }

    private DetectCodeLocation createSimpleCodeLocation(NameVersion projectNameVersion, File sourcePath) {
        return DetectCodeLocation.forCreator(
            new BasicDependencyGraph(),
            sourcePath,
            ExternalIdCreator.nameVersion(CodeLocationConverter.DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion()),
            SCAN_CREATOR
        );
    }
}
