package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Conan CLI", language = "C/C++", forge = "conan", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: conanfile.txt or conanfile.py. Executable: conan.")
public class ConanCliDetectable extends Detectable {
    public static final String CONANFILETXT = "conanfile.txt";
    public static final String CONANFILEPY = "conanfile.py";
    private final FileFinder fileFinder;
    private final ConanResolver conanResolver;
    private final ConanCliExtractor conanCliExtractor;

    private ExecutableTarget conanExe;

    public ConanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanResolver conanResolver, ConanCliExtractor conanCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanResolver = conanResolver;
        this.conanCliExtractor = conanCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
        File conanTxtFile = fileFinder.findFile(environment.getDirectory(), CONANFILETXT);
        if (conanTxtFile == null) {
            File conanPyFile = fileFinder.findFile(environment.getDirectory(), CONANFILEPY);
            if (conanPyFile == null) {
                return new FileNotFoundDetectableResult(CONANFILETXT);
            } else {
                passedResultBuilder.foundFile(conanPyFile);
            }
        } else {
            passedResultBuilder.foundFile(conanTxtFile);
        }
        return passedResultBuilder.build();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        conanExe = requirements.executable(() -> conanResolver.resolveConan(environment), "conan");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return conanCliExtractor.extract(environment.getDirectory(), conanExe);
    }
}
