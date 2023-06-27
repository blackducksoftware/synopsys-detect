package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.NameVersion;

public class CreateScanBatchOperation {
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final DirectoryManager directoryManager;
    private final CodeLocationNameManager codeLocationNameManager;

    public CreateScanBatchOperation(BlackDuckSignatureScannerOptions signatureScannerOptions, DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager) {
        this.signatureScannerOptions = signatureScannerOptions;
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public ScanBatch createScanBatchWithBlackDuck(
        NameVersion projectNameVersion,
        List<SignatureScanPath> signatureScanPaths,
        BlackDuckServerConfig blackDuckServerConfig,
        @Nullable DockerTargetData dockerTargetData
    )
        throws DetectUserFriendlyException {
        return createScanBatch(projectNameVersion, signatureScanPaths, blackDuckServerConfig, dockerTargetData);
    }

    public ScanBatch createScanBatchWithoutBlackDuck(NameVersion projectNameVersion, List<SignatureScanPath> signatureScanPaths, @Nullable DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException {
        //when offline, we must still call this with 'null' as a workaround for library issues, so offline scanner must be created with this set to null.
        return createScanBatch(projectNameVersion, signatureScanPaths, null, dockerTargetData);
    }

    private ScanBatch createScanBatch(
        NameVersion projectNameVersion,
        List<SignatureScanPath> signatureScanPaths,
        @Nullable BlackDuckServerConfig blackDuckServerConfig,
        @Nullable DockerTargetData dockerTargetData
    )
        throws DetectUserFriendlyException {
        ScanBatchBuilder scanJobBuilder = new ScanBatchBuilder();
        scanJobBuilder.scanMemoryInMegabytes(signatureScannerOptions.getScanMemory());
        scanJobBuilder.outputDirectory(directoryManager.getScanOutputDirectory());

        scanJobBuilder.dryRun(signatureScannerOptions.getDryRun());
        scanJobBuilder.cleanupOutput(false);

        signatureScannerOptions.getSnippetMatching().ifPresent(scanJobBuilder::snippetMatching);
        scanJobBuilder.uploadSource(signatureScannerOptions.getUploadSource());
        scanJobBuilder.licenseSearch(signatureScannerOptions.getLicenseSearch());
        scanJobBuilder.copyrightSearch(signatureScannerOptions.getCopyrightSearch());

        signatureScannerOptions.getAdditionalArguments().ifPresent(scanJobBuilder::additionalScanArguments);
        
        scanJobBuilder.rapid(signatureScannerOptions.getIsStateless());
        
        scanJobBuilder.bomCompareMode(signatureScannerOptions.getBomCompareMode().toString());

        String projectName = projectNameVersion.getName();
        String projectVersionName = projectNameVersion.getVersion();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName);

        signatureScannerOptions.getIndividualFileMatching()
            .ifPresent(scanJobBuilder::individualFileMatching);
        
        signatureScannerOptions.getReducedPersistence()
            .ifPresent(scanJobBuilder::reducedPersistence);

        File sourcePath = directoryManager.getSourceDirectory();

        for (SignatureScanPath scanPath : signatureScanPaths) {
            File dockerTarget = null;
            if (dockerTargetData != null) {
                dockerTarget = dockerTargetData.getSquashedImage().orElse(dockerTargetData.getProvidedImageTar().orElse(null));
            }
            String codeLocationName = codeLocationNameManager.createScanCodeLocationName(
                sourcePath,
                scanPath.getTargetPath(),
                dockerTarget,
                projectName,
                projectVersionName
            );
            scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanPath.getTargetCanonicalPath(), scanPath.getExclusions(), codeLocationName));
        }

        scanJobBuilder.fromBlackDuckServerConfig(blackDuckServerConfig);//when offline, we must still call this with 'null' as a workaround for library issues, so offline scanner must be created with this set to null.
        try {
            return scanJobBuilder.build();
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

}
