package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.EncryptionException;
import com.synopsys.integration.util.NameVersion;

public class OnlineBlackDuckSignatureScanner extends BlackDuckSignatureScanner {

    private final HubServerConfig hubServerConfig;

    public OnlineBlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder,
        final CodeLocationNameManager codeLocationNameManager, final BlackDuckSignatureScannerOptions signatureScannerOptions, final EventSystem eventSystem, final ScanJobManager scanJobManager,
        final HubServerConfig hubServerConfig) {
        super(directoryManager, detectFileFinder, codeLocationNameManager, signatureScannerOptions, eventSystem, scanJobManager);
        this.hubServerConfig = hubServerConfig;
    }

    @Override
    protected ScanJob createScanJob(NameVersion projectNameVersion, List<SignatureScanPath> signatureScanPaths, File dockerTarFile) {
        final ScanJobBuilder scanJobBuilder = createDefaultScanJobBuilder(projectNameVersion, signatureScanPaths, dockerTarFile);
        try {
            scanJobBuilder.fromHubServerConfig(hubServerConfig);
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
        return scanJobBuilder.build();
    }
}
