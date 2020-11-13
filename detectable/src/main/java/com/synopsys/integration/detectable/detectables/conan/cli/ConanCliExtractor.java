package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.extraction.Extraction;

public class ConanCliExtractor {

    public ConanCliExtractor(DetectableExecutableRunner executableRunner, ConanCliCodeLocationPackager conanCliCodeLocationPackager) {

    }

    public Extraction extract(File projectDir, File conanExe, ConanCliExtractorOptions conanCliExtractorOptions) {
        return new Extraction.Builder().failure("not yet implemented").build();
    }
}
