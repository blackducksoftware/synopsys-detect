package com.synopsys.integration.detectable.detectables.xcode.parse;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;
import com.synopsys.integration.detectable.util.FileFormatChecker;

public class XcodeWorkspaceFormatChecker extends FileFormatChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String[] KNOWN_FILE_FORMAT_VERSIONS = { "1.0" };

    public XcodeWorkspaceFormatChecker() {
        super(KNOWN_FILE_FORMAT_VERSIONS);
    }

    public boolean checkForVersionCompatibility(XcodeWorkspace xcodeWorkspace) {
        return checkForVersionCompatibility(xcodeWorkspace::getFormatVersion);
    }

    @Override
    public void handleUnknownVersion(@Nullable String unknownFileFormat) {
        logger.warn(String.format(
            "The format version of Package.resolved (%s) is unknown to Detect, but processing will continue. Known format versions are (%s).",
            unknownFileFormat,
            StringUtils.join(KNOWN_FILE_FORMAT_VERSIONS, ", ")
        ));
    }
}
