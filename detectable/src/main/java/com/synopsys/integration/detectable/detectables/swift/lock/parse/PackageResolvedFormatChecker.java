package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolved;
import com.synopsys.integration.detectable.util.FileFormatChecker;

public class PackageResolvedFormatChecker extends FileFormatChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String[] KNOWN_FILE_FORMAT_VERSIONS = { "1" };

    public PackageResolvedFormatChecker() {
        super(KNOWN_FILE_FORMAT_VERSIONS);
    }

    public boolean checkForVersionCompatibility(PackageResolved packageResolved) {
        return checkForVersionCompatibility(packageResolved::getFileFormatVersion);
    }

    @Override
    public void handleUnknownVersion(String unknownFileFormat) {
        logger.warn(String.format(
            "The format version of Package.resolved (%s) is unknown to Detect, but processing will continue. Known format versions are (%s).",
            unknownFileFormat,
            StringUtils.join(KNOWN_FILE_FORMAT_VERSIONS, ", ")
        ));
    }
}
