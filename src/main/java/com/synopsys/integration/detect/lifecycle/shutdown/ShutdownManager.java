/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.lifecycle.boot.DetectBootResult;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CleanupUtility cleanupUtility;

    public ShutdownManager(final CleanupUtility cleanupUtility) {
        this.cleanupUtility = cleanupUtility;
    }

    public void shutdown(DetectBootResult detectBootResult, ShutdownDecision shutdownDecision) {
        if (shutdownDecision.getPhoneHomeManager() != null) {
            try {
                logger.debug("Ending phone home.");
                shutdownDecision.getPhoneHomeManager().endPhoneHome();
            } catch (Exception e) {
                logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
            }
        }

        if (shutdownDecision.getDiagnosticSystem() != null) {
            shutdownDecision.getDiagnosticSystem().finish();
        }

        if (detectBootResult.getDirectoryManager().isPresent() && shutdownDecision.getCleanupDecision().shouldCleanup()) {
            DirectoryManager directoryManager = detectBootResult.getDirectoryManager().get();
            try {
                List<File> cleanupToSkip = determineSkippedCleanupFiles(shutdownDecision.getCleanupDecision(), directoryManager);
                cleanupUtility.cleanup(directoryManager.getRunHomeDirectory(), cleanupToSkip);
            } catch (Exception e) {
                logger.debug("Error trying cleanup: ", e);
            }
        } else {
            logger.info("Skipping cleanup, it is disabled.");
        }
    }

    private List<File> determineSkippedCleanupFiles(CleanupDecision cleanupDecision, DirectoryManager directoryManager) {
        logger.debug("Detect will cleanup.");
        List<File> cleanupToSkip = new ArrayList<>();
        if (cleanupDecision.shouldPreserveScan()) {
            logger.debug("Will not cleanup scan folder.");
            cleanupToSkip.add(directoryManager.getScanOutputDirectory());
        }
        if (cleanupDecision.shouldPreserveBdio()) {
            logger.debug("Will not cleanup bdio folder.");
            cleanupToSkip.add(directoryManager.getBdioOutputDirectory());
            logger.debug("Will not cleanup impact analysis folder.");
            cleanupToSkip.add(directoryManager.getImpactAnalysisOutputDirectory());
        }
        if (cleanupDecision.shouldPreserveAirGap()) {
            logger.debug("Will not cleanup Air Gap file.");
            cleanupToSkip.add(cleanupDecision.getAirGapZip());
        }
        return cleanupToSkip;
    }
}
