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
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "GitHub", requirementsMarkdown = "Files: Cartfile, Cartfile.resolved")
public class CarthageDetectable extends Detectable {
    private static final String CARTFILE_FILENAME = "Cartfile";
    private static final String CARTFILE_RESOLVED_FILENAME = "Cartfile.resolved";

    private final FileFinder fileFinder;
    private final CarthageExtractor carthageExtractor;

    private File cartfile;
    private File cartfileResolved;

    public CarthageDetectable(DetectableEnvironment environment, FileFinder fileFinder, CarthageExtractor carthageExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.carthageExtractor = carthageExtractor;
    }

    @Override
    public DetectableResult applicable() { //TODO: Use the new requirements eitherFile.
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(CARTFILE_FILENAME, CARTFILE_RESOLVED_FILENAME, foundCartfile -> cartfile = foundCartfile, foundCartfileResolved -> cartfileResolved = foundCartfileResolved);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (cartfileResolved == null && cartfile != null) { //TODO: See if we can express this better. Can be difficult to parse the meaning, maybe a Requirements.requiresLock()
            return new CartfileResolvedNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return carthageExtractor.extract(cartfileResolved);
    }
}
