package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;
import com.synopsys.integration.detectable.util.FileFormatChecker;

public class PackageResolvedFormatChecker extends FileFormatChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final PackageResolvedFormat[] KNOWN_FILE_FORMAT_VERSIONS = { PackageResolvedFormat.V_1, PackageResolvedFormat.V_2 };

    public static PackageResolvedFormat[] getKnownFileFormatVersions() {
        return KNOWN_FILE_FORMAT_VERSIONS;
    }

    public PackageResolvedFormatChecker() {
        super(Arrays.stream(KNOWN_FILE_FORMAT_VERSIONS)
            .map(PackageResolvedFormat::getVersionString)
            .toArray(String[]::new)
        );
    }

    public boolean checkForVersionCompatibility(PackageResolvedFormat format) {
        return checkForVersionCompatibility(format.getVersionString());
    }

    @Override
    public void handleUnknownVersion(String unknownFileFormat) {
        logger.warn(String.format(
            "The format version of Package.resolved (version: %s) is unknown to Detect. Known format versions are (%s). Processing will continue with the latest known format. If errors occur, please contact support.",
            unknownFileFormat,
            StringUtils.join(getKnownFileFormats(), ", ")
        ));
    }
}
