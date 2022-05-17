package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.lifecycle.OperationException;
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

    public Object runSigmaOnline(NameVersion projectNameVersion, Bdio2FileUploadService bdio2FileUploadService)
        throws OperationException, IntegrationException, InterruptedException {
        List<File> sigmaScanTargets = operationFactory.calculateSigmaScanTargets();
        for (File target : sigmaScanTargets) {
            String scanId = initiateScan(projectNameVersion, target, bdio2FileUploadService);
            //TODO- implement rest of it
        }
        return null;
    }

    public void runSigmaOffline() {

    }

    //TODO- should this be extracted out of Sigma context?
    public String initiateScan(NameVersion projectNameVersion, File sourcePath, Bdio2FileUploadService bdio2FileUploadService)
        throws OperationException, IntegrationException, InterruptedException {
        DetectCodeLocation codeLocation = createSimpleCodeLocation(projectNameVersion, sourcePath);
        BdioCodeLocationResult bdioCodeLocationResult = operationFactory.createBdioCodeLocationsFromDetectCodeLocations(
            Collections.singletonList(codeLocation),
            projectNameVersion
        );
        UploadTarget uploadTarget = operationFactory.createBdio2Files(bdioCodeLocationResult, projectNameVersion).get(0);
        return null;
        //return bdio2FileUploadService.uploadFile(uploadTarget).getScanId(); TODO- uncomment when BD 63.0.3 is released
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
