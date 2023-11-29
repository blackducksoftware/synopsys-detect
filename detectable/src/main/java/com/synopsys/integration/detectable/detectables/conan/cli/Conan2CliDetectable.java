package com.synopsys.integration.detectable.detectables.conan.cli;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.conan2.ConanGraphInfoParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(
    name = "Conan 2 CLI", language = "C/C++", forge = "conan", accuracy = DetectableAccuracyType.HIGH,
    requirementsMarkdown = "Files: conanfile.txt or conanfile.py. Executable: conan (version 2.x)"
)
public class Conan2CliDetectable extends ConanBaseCliDetectable {
    private final ConanGraphInfoParser conanGraphInfoParser;

    public Conan2CliDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        ConanResolver conanResolver,
        ConanCliExtractor conanCliExtractor,
        ConanGraphInfoParser conanGraphInfoParser)
    {
        super(environment, fileFinder, conanResolver, conanCliExtractor);
        this.conanGraphInfoParser = conanGraphInfoParser;
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, DetectableException {
        return conanGraphInfoParser.parse(conanCliExtractor.extractGraphInfoFromConan2(environment.getDirectory(), conanExe));
    }

    @Override
    protected String getExpectedMajorConanVersion() {
        return "2";
    }
}
