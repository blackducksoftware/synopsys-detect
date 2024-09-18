package com.blackduck.integration.detectable.detectables.conan.cli;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.detectables.conan.cli.parser.conan2.ConanGraphInfoParser;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

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
