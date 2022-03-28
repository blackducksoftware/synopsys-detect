package com.synopsys.integration.detect.tool.signaturescanner;

import com.synopsys.integration.detect.tool.signaturescanner.enums.SignatureScanStatusType;

public class SignatureScannerReportTestUtil {
    public static SignatureScannerReport successfulReport(String codeLocationName, int notificationCount) {
        return new SignatureScannerReport(new SignatureScanPath(), SignatureScanStatusType.SUCCESS, 0, null, null, true, codeLocationName, notificationCount);
    }

    public static SignatureScannerReport skippedReport(String codeLocationName, int notificationCount) {
        return new SignatureScannerReport(new SignatureScanPath(), SignatureScanStatusType.SKIPPED, 2, null, null, true, codeLocationName, notificationCount);
    }
}
