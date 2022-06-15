package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.bdio2.Bdio;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.sigma.SigmaCodeLocationData;
import com.synopsys.integration.detect.tool.sigma.SigmaReport;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detectable.util.ExternalIdCreator;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class SigmaScanStepRunner {
    private static final String SCAN_CREATOR = "sigma";

    private final OperationFactory operationFactory;
    private final IntegrationEscapeUtil integrationEscapeUtil; //TODO- IntegrationEscapeUtil's methods should be static

    public SigmaScanStepRunner(
        OperationFactory operationFactory
    ) {
        this.operationFactory = operationFactory;
        this.integrationEscapeUtil = new IntegrationEscapeUtil();
    }

    public SigmaCodeLocationData runSigmaOnline(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws OperationException, IntegrationException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();

        File sigmaExe;
        Optional<File> localSigma = operationFactory.calculateUserProvidedSigmaPath();
        if (localSigma.isPresent()) {
            sigmaExe = localSigma.get();
            validateSigma(sigmaExe);
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

        Set<String> codeLocationNames = sigmaReports.stream()
            .map(SigmaReport::getCodeLocationName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        return new SigmaCodeLocationData(codeLocationNames);
    }

    public void runSigmaOffline() throws OperationException, IntegrationException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();
        File sigmaExe = operationFactory.calculateUserProvidedSigmaPath()
            .orElseThrow(() -> new IntegrationException("Was not able to install or locate Sigma.  Must either connect to a Black Duck or provide a path to a local Sigma."));
        validateSigma(sigmaExe);
        List<SigmaReport> sigmaReports = new LinkedList<>();
        int count = 0;
        for (File scanTarget : sigmaScanTargets) {
            SigmaReport sigmaReport = performOfflineScan(scanTarget, sigmaExe, count++);
            sigmaReports.add(sigmaReport);
        }
        operationFactory.publishSigmaReport(sigmaReports);
    }

    private void validateSigma(File sigmaExe) throws IntegrationException {
        if (!sigmaExe.exists()) {
            throw new IntegrationException(String.format("Provided Sigma %s does not exist.", sigmaExe.getAbsolutePath()));
        }
    }

    public SigmaReport performOnlineScan(
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        File sigmaExe,
        File scanTarget,
        int count
    ) {
        try {
            File resultsFile = operationFactory.performSigmaScan(scanTarget, sigmaExe, count);
            String codeLocationName = operationFactory.createSigmaCodeLocationName(scanTarget, projectNameVersion);
            String scanId = initiateScan(projectNameVersion, scanTarget, blackDuckRunData.getBlackDuckServicesFactory().createBdio2FileUploadService(), codeLocationName);
            operationFactory.uploadSigmaResults(blackDuckRunData, resultsFile, scanId);
            return SigmaReport.SUCCESS_ONLINE(scanTarget, codeLocationName);
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
        return SigmaReport.SUCCESS_OFFLINE(scanTarget);
    }

    //TODO- look into extracting scan initiation to another class

    //TODO- this should only be necessary if we didn't already upload BDIO during DETECTORS phase
    public String initiateScan(NameVersion projectNameVersion, File sourcePath, Bdio2FileUploadService bdio2FileUploadService, String codeLocationNameOverride)
        throws OperationException, IntegrationException {
        DetectCodeLocation codeLocation = createSimpleCodeLocation(projectNameVersion, sourcePath);
        BdioCodeLocationResult bdioCodeLocationResult = overrideBdioCodeLocationResult(codeLocationNameOverride, operationFactory.createBdioCodeLocationsFromDetectCodeLocations(
            Collections.singletonList(codeLocation),
            projectNameVersion
        ));

        UploadTarget uploadTarget = operationFactory.createBdio2Files(bdioCodeLocationResult, projectNameVersion, Bdio.ScanType.INFRASTRUCTURE_AS_CODE)
            .get(0);
        return bdio2FileUploadService.uploadFileAndGetResult(uploadTarget).getScanId();
    }

    private DetectCodeLocation createSimpleCodeLocation(NameVersion projectNameVersion, File sourcePath) {
        return DetectCodeLocation.forCreator(
            new BasicDependencyGraph(),
            sourcePath,
            ExternalIdCreator.nameVersion(CodeLocationConverter.DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion()),
            SCAN_CREATOR
        );
    }

    private BdioCodeLocationResult overrideBdioCodeLocationResult(String codeLocationNameOverride, BdioCodeLocationResult original) {
        List<BdioCodeLocation> bdioCodeLocations = original.getBdioCodeLocations().stream()
            .map(bdioCodeLocation -> new BdioCodeLocation(bdioCodeLocation.getDetectCodeLocation(), codeLocationNameOverride, createBdioName(codeLocationNameOverride)))
            .collect(Collectors.toList());
        Map<DetectCodeLocation, String> codeLocationNameMap = new HashMap<>();
        Map.Entry<DetectCodeLocation, String> entry = original.getCodeLocationNames().entrySet().iterator().next();
        codeLocationNameMap.put(entry.getKey(), codeLocationNameOverride);

        return new BdioCodeLocationResult(bdioCodeLocations, codeLocationNameMap);
    }

    private String createBdioName(String codeLocationName) {
        String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] { "/", "\\", " " }, new String[] { "_", "_", "_" });
        return integrationEscapeUtil.replaceWithUnderscore(filenameRaw);
    }
}
