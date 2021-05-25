/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;

public class CreateSignatureScanReports {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<SignatureScannerReport> reportResults(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList) {
        List<SignatureScannerReport> signatureScannerReports = new ArrayList<>();
        for (SignatureScanPath signatureScanPath : signatureScanPaths) {
            Optional<ScanCommandOutput> scanCommandOutput = scanCommandOutputList.stream()
                                                                .filter(output -> output.getScanTarget().equals(signatureScanPath.getTargetCanonicalPath()))
                                                                .findFirst();
            SignatureScannerReport signatureScannerReport = SignatureScannerReport.create(signatureScanPath, scanCommandOutput.orElse(null));
            signatureScannerReports.add(signatureScannerReport);
        }
        return signatureScannerReports;
    }
}
