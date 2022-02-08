package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;

public interface PathResolver {
    Path resolvePath(String filePath);
}

