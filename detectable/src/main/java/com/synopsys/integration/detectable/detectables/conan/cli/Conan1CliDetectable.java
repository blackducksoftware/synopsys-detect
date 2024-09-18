package com.synopsys.integration.detectable.detectables.conan.cli;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(
    name = "Conan 1 CLI", language = "C/C++", forge = "conan", accuracy = DetectableAccuracyType.HIGH,
    requirementsMarkdown = "Files: conanfile.txt or conanfile.py. Executable: conan (version 1.x)."
)
public class Conan1CliDetectable extends ConanBaseCliDetectable {

    public Conan1CliDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        ConanResolver conanResolver,
        ConanCliExtractor conanCliExtractor)
    {
        super(environment, fileFinder, conanResolver, conanCliExtractor);
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return conanCliExtractor.extractFromConan1(environment.getDirectory(), conanExe);
    }

    @Override
    protected String getExpectedMajorConanVersion() {
        return "1";
    }
}
