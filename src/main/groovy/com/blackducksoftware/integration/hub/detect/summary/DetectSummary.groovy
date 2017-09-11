/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.summary

import java.util.Map.Entry

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.log.IntLogger

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectSummary {
    private Map<BomToolType, Result> bomToolResults
    private Map<File, Result> scanResults


    public void addApplicableBomToolType(BomToolType bomToolType) {
        bomToolResults.put(bomToolType, Result.FAILURE)
    }

    public void setBomToolResult(BomToolType bomToolType, Result result) {
        bomToolResults.put(bomToolType, result)
    }

    public void addPathToBeScanned(File scanPath) {
        scanResults.put(scanPath, Result.FAILURE)
    }

    public void setPathScanResult(File scanPath, Result result) {
        scanResults.put(scanPath, result)
    }

    public void logResults(IntLogger logger) {
        for (Entry<BomToolType, Result> entry : bomToolResults.entrySet()) {
            logger.info("${entry.getKey()} : ${entry.getValue()}")
        }
        for (Entry<File, Result> entry : scanResults.entrySet()) {
            logger.info("Scan Target ${entry.getKey()} : ${entry.getValue()}")
        }
    }
}
