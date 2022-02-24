package com.synopsys.integration.detectable.detectables.carthage;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.CartfileResolvedNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "GitHub", requirementsMarkdown = "Files: Cartfile, Cartfile.resolved")
public class CarthageDetectable extends Detectable {
    private static final String CARTFILE_FILENAME = "Cartfile";
    private static final String CARTFILE_RESOLVED_FILENAME = "Cartfile.resolved";

    private final FileFinder fileFinder;
    private final CarthageExtractor carthageExtractor;

    private File cartfileResolved;

    public CarthageDetectable(DetectableEnvironment environment, FileFinder fileFinder, CarthageExtractor carthageExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.carthageExtractor = carthageExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(
            CARTFILE_FILENAME,
            CARTFILE_RESOLVED_FILENAME,
            foundCartfile -> {}, // Used only to identify Carthage projects
            foundCartfileResolved -> cartfileResolved = foundCartfileResolved
        );
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(CARTFILE_RESOLVED_FILENAME, () -> new CartfileResolvedNotFoundDetectableResult(environment.getDirectory().getAbsolutePath()));
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return carthageExtractor.extract(cartfileResolved);
    }
}
