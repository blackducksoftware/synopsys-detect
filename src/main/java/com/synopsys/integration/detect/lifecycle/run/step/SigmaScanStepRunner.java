package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.Bdio;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detectable.util.ExternalIdCreator;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class SigmaScanStepRunner {
    private static final String SCAN_CREATOR = "detect";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OperationFactory operationFactory;

    public SigmaScanStepRunner(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    //TODO- need runSigma methods to return some result or pubilsh something to let Detect know if they were successful or not

    public Object runSigmaOnline(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws OperationException, IntegrationException, InterruptedException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();
        File sigmaExe = resolveSigma(blackDuckRunData);
        for (File scanTarget : sigmaScanTargets) {
            String scanId = initiateScan(projectNameVersion, scanTarget, blackDuckRunData.getBlackDuckServicesFactory().createBdio2FileUploadService());
            Optional<File> sigmaScanResultFile = operationFactory.performSigmaScan(scanTarget, sigmaExe);
            if (sigmaScanResultFile.isPresent()) {
                operationFactory.uploadSigmaResults(blackDuckRunData, sigmaScanResultFile.get(), scanId);
            }
        }
        return null;
    }

    public void runSigmaOffline() throws OperationException, IntegrationException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();
        File sigmaExe = resolveSigma(null);
        for (File scanTarget : sigmaScanTargets) {
            Optional<File> sigmaScanResultFile = operationFactory.performSigmaScan(scanTarget, sigmaExe);
            sigmaScanResultFile.ifPresent(resultsFile -> {
                    //TODO- publish successful scan
                }
            );
        }
    }

    private File resolveSigma(@Nullable BlackDuckRunData blackDuckRunData) throws OperationException, IntegrationException {
        Optional<File> localInstall = operationFactory.calculateOnlineLocalSigmaInstallPath();
        if (localInstall.isPresent()) {
            return operationFactory.resolveSigmaFromLocalInstall(localInstall.get());
        } else if (blackDuckRunData != null) {
            return operationFactory.resolveSigmaOnline(blackDuckRunData);
        } else {
            throw new IntegrationException("Was not able to install or locate Sigma.  Must either connect to a Black Duck or provide a path to a local Sigma.");
        }
    }

    //TODO- should this be extracted out of Sigma context?
    public String initiateScan(NameVersion projectNameVersion, File sourcePath, Bdio2FileUploadService bdio2FileUploadService)
        throws OperationException, IntegrationException, InterruptedException {
        DetectCodeLocation codeLocation = createSimpleCodeLocation(projectNameVersion, sourcePath);
        BdioCodeLocationResult bdioCodeLocationResult = operationFactory.createBdioCodeLocationsFromDetectCodeLocations(
            Collections.singletonList(codeLocation),
            projectNameVersion
        ); //TODO- in this operation the code location name is created (could override in a hacky way)
        UploadTarget uploadTarget = operationFactory.createBdio2Files(bdioCodeLocationResult, projectNameVersion, Bdio.ScanType.INFRASTRUCTURE_AS_CODE).get(0);
        return bdio2FileUploadService.uploadFile(uploadTarget).getScanId();
    }

    //TODO- name is awful
    private DetectCodeLocation createSimpleCodeLocation(NameVersion projectNameVersion, File sourcePath) {
        return DetectCodeLocation.forCreator(
            new BasicDependencyGraph(),
            sourcePath,
            ExternalIdCreator.nameVersion(CodeLocationConverter.DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion()),
            SCAN_CREATOR
        );
    }
}
