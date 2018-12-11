package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;

public class CodeLocationWaitData {
    private NotificationTaskRange bdioUploadRange;
    private Set<String> bdioUploadCodeLocationNames;
    private boolean hasBdioResults;

    private NotificationTaskRange signatureScanRange;
    private Set<String> signatureScanCodeLocationNames;
    private boolean hasScanResults;

    public void setFromBdioCodeLocationCreationData(CodeLocationCreationData<UploadBatchOutput> bdioCodeLocationCreationData) {
        bdioUploadRange = bdioCodeLocationCreationData.getNotificationTaskRange();
        bdioUploadCodeLocationNames = bdioCodeLocationCreationData.getSuccessfulCodeLocationNames();
        hasBdioResults = true;
    }

    public void setFromSignatureScannerCodeLocationCreationData(CodeLocationCreationData<ScanBatchOutput> scanCodeLocationCreationData) {
        signatureScanRange = scanCodeLocationCreationData.getNotificationTaskRange();
        signatureScanCodeLocationNames = scanCodeLocationCreationData.getSuccessfulCodeLocationNames();
        hasScanResults = true;
    }

    public NotificationTaskRange getBdioUploadRange() {
        return bdioUploadRange;
    }

    public Set<String> getBdioUploadCodeLocationNames() {
        return bdioUploadCodeLocationNames;
    }

    public boolean hasBdioResults() {
        return hasBdioResults;
    }

    public NotificationTaskRange getSignatureScanRange() {
        return signatureScanRange;
    }

    public Set<String> getSignatureScanCodeLocationNames() {
        return signatureScanCodeLocationNames;
    }

    public boolean hasScanResults() {
        return hasScanResults;
    }

}
