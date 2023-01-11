/*
 * Copyright (C) 2023 Synopsys Inc.
 * http://www.synopsys.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Synopsys ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Synopsys.
 */
package com.synopsys.integration.detect.workflow.bdba;

public class BdbaStatusScanView {
// {"scannedFiles":263,"scannedBytes":17656539,"knownFiles":262,"knownBytes":13462235,"status":"ready","scanId":"2ffbe424-9a3a-4951-8c19-d22852d37428"}
    
    private int scannedFiles;
    private long scannedBytes;
    private int knownFiles;
    private long knownBytes;
    private String status;
    private String scanId;
    
    public int getScannedFiles() {
        return scannedFiles;
    }
    public void setScannedFiles(int scannedFiles) {
        this.scannedFiles = scannedFiles;
    }
    public long getScannedBytes() {
        return scannedBytes;
    }
    public void setScannedBytes(long scannedBytes) {
        this.scannedBytes = scannedBytes;
    }
    public int getKnownFiles() {
        return knownFiles;
    }
    public void setKnownFiles(int knownFiles) {
        this.knownFiles = knownFiles;
    }
    public long getKnownBytes() {
        return knownBytes;
    }
    public void setKnownBytes(long knownBytes) {
        this.knownBytes = knownBytes;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getScanId() {
        return scanId;
    }
    public void setScanId(String scanId) {
        this.scanId = scanId;
    }
    
    
}
