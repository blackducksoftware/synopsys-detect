package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class RlScanStepRunner {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NameVersion projectNameVersion;
    private UUID scanId;
    private final OperationRunner operationRunner;
    private final BlackDuckRunData blackDuckRunData;
    private String codeLocationName;
    private static final BlackDuckVersion MIN_BLACK_DUCK_VERSION = new BlackDuckVersion(2023, 10, 0);
            // TODO true version but no servers with this right now, new BlackDuckVersion(2024, 4, 0);
    
    public RlScanStepRunner(OperationRunner operationRunner, BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion) {
        this.operationRunner = operationRunner;
        this.blackDuckRunData = blackDuckRunData;
        this.projectNameVersion = projectNameVersion;
    }

    public Optional<UUID> invokeRlWorkflow() throws IntegrationException {
        logger.debug("Determining if configuration is valid to run a ReversingLabs scan.");
        if (!isReversingLabsEligible()) {
            logger.info("No detect.rl.scan.file.path property was provided. Skipping ReversingLabs scan.");
            return Optional.ofNullable(scanId);
        }
        if (!isBlackDuckVersionValid()) {
            String minBlackDuckVersion = String.join(".",
                Integer.toString(MIN_BLACK_DUCK_VERSION.getMajor()),
                Integer.toString(MIN_BLACK_DUCK_VERSION.getMinor()),
                Integer.toString(MIN_BLACK_DUCK_VERSION.getPatch())
            );
            throw new IntegrationException("ReversingLabs scan is only supported with BlackDuck version " + minBlackDuckVersion + " or greater. ReversingLabs scan could not be run.");
        }
        
        // Generate BDIO header
        codeLocationName = createCodeLocationName();
        initiateScan();
        
        return null;
    }
    
    private void initiateScan() {
        // TODO Auto-generated method stub
        
    }

    private boolean isBlackDuckVersionValid() {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_BLACK_DUCK_VERSION);
    }
    
    private boolean isReversingLabsEligible() {
        return operationRunner.getRlScanFilePath().isPresent();
    }
    
    private String createCodeLocationName() {
        CodeLocationNameManager codeLocationNameManager = operationRunner.getCodeLocationNameManager();
        File targetFile = new File(operationRunner.getRlScanFilePath().get());
        return codeLocationNameManager.createReversingLabsScanCodeLocationName(targetFile, projectNameVersion.getName(), projectNameVersion.getVersion());
    }

}
