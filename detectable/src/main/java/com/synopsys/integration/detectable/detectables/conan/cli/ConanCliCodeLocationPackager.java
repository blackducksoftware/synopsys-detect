package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class ConanCliCodeLocationPackager {
    private final ExternalIdFactory externalIdFactory;

    public ConanCliCodeLocationPackager(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public List<ConanParseResult> extractCodeLocations(String sourcePath, List<String> conanOutput) {
        return Arrays.asList(new ConanParseResult());
    }
}
