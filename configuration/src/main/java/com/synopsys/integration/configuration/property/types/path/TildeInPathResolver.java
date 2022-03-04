package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TildeInPathResolver implements PathResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String systemUserHome;

    public TildeInPathResolver(String systemUserHome) {
        this.systemUserHome = systemUserHome;
    }

    /**
     * Resolves a '~' character at the start of [filePath]. In linux/mac environments, this
     * is shorthand for the user's home directory. If we encounter a property that
     * is formed this way, we can resolve it.
     */
    @Override
    public Path resolvePath(String filePath) {
        String resolved;
        if (filePath.startsWith("~/")) {
            resolved = systemUserHome + filePath.substring(1);
        } else {
            resolved = filePath;
        }

        if (!resolved.equals(filePath)) {
            // TODO: Add callback for this? Properties should not be explicitly referenced in the configuration module.
            logger.trace(String.format(
                "We have resolved %s to %s. If this is not expected, please revise the path provided, or specify --detect.resolve.tilde.in.paths=false.",
                filePath,
                resolved
            ));
        }

        return Paths.get(resolved);
    }
}

