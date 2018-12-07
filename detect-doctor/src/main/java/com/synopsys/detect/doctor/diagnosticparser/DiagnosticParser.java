package com.synopsys.detect.doctor.diagnosticparser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.DetectZipUtil;
import com.synopsys.detect.doctor.DoctorException;
import com.synopsys.detect.doctor.run.DoctorDirectoryManager;

public class DiagnosticParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiagnosticParser() {

    }

    public DetectRunInfo processDiagnosticZip(DoctorDirectoryManager doctorDirectoryManager, File diagnosticZip) throws DoctorException, IOException {

        File cache = doctorDirectoryManager.getDiagnosticCacheDirectory();
        String zipName = FilenameUtils.getBaseName(diagnosticZip.getName());
        File unzippedDirectory = new File(cache, zipName);

        if (unzippedDirectory.exists()) {
            logger.info("Zip cache exists. Using that: " + unzippedDirectory.getAbsolutePath());
        } else {
            logger.info("Preparing to unzip to: " + unzippedDirectory.getAbsolutePath());
            DetectZipUtil.unzip(diagnosticZip, unzippedDirectory);
            logger.info("Unzip complete.");
        }

        logger.info("Verifying this is a detect zip by finding log.");
        File logsDirectory = new File(unzippedDirectory, "logs");
        File log = new File(logsDirectory, "out.txt");

        if (log.exists()) {
            logger.info("Found log! ");
        } else {
            throw new DoctorException("Log file not found. Not a diagnostic zip or unsupported format.");
        }
        File extractions = new File(unzippedDirectory, "extractions");
        if (extractions.exists()) {
            logger.info("Found extractions!");
        } else {
            throw new DoctorException("Runs file not found. Not a diagnostic zip or unsupported format.");
        }

        return new DetectRunInfo(log, extractions);
    }
}
