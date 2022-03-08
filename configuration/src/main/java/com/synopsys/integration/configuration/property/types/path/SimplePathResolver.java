package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimplePathResolver implements PathResolver {
    @Override
    public Path resolvePath(String filePath) {
        return Paths.get(filePath).toAbsolutePath();
    }
}