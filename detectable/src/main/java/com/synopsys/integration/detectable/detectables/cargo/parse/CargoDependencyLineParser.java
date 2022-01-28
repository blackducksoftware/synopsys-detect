package com.synopsys.integration.detectable.detectables.cargo.parse;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.util.NameOptionalVersion;

public class CargoDependencyLineParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<NameOptionalVersion> parseDependencyName(String dependencyLine) {
        String[] dependencyLinePieces = dependencyLine.split(" ");
        if (dependencyLinePieces.length == 0 || StringUtils.isEmpty(dependencyLine)) {
            logger.warn("Failed to parse dependency line. It will be ignored: {}", dependencyLine);
            return Optional.empty();
        }
        String name = StringUtils.trim(dependencyLinePieces[0]);
        if (dependencyLinePieces.length >= 2) {
            String version = StringUtils.trimToNull(dependencyLinePieces[1]);
            return Optional.of(new NameOptionalVersion(name, version));
        } else {
            return Optional.of(new NameOptionalVersion(name));
        }
    }
}
