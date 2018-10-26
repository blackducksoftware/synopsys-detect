package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.EncryptionException;
import com.synopsys.integration.util.NameVersion;

public class OfflineBlackDuckSignatureScanner extends BlackDuckSignatureScanner {

    public OfflineBlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder,
        final CodeLocationNameManager codeLocationNameManager, final BlackDuckSignatureScannerOptions signatureScannerOptions, final EventSystem eventSystem, final ScanJobManager scanJobManager) {
        super(directoryManager, detectFileFinder, codeLocationNameManager, signatureScannerOptions, eventSystem, scanJobManager);
    }

    @Override
    protected ScanJob createScanJob(final NameVersion projectNameVersion, final List<SignatureScanPath> signatureScanPaths, final File dockerTarFile) {
        final ScanJobBuilder scanJobBuilder = createDefaultScanJobBuilder(projectNameVersion, signatureScanPaths, dockerTarFile);
        try {
            scanJobBuilder.fromHubServerConfig(null);//temporarily need to do this. fix when hub common updates;
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
        return scanJobBuilder.build();
    }
}
