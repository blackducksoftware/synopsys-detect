package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

// "conan info ." might (if project name/version are defined by conanfile.py) produce: conanfile.py (hello/1.0)

@DetectableInfo(language = "C/C++", forge = "conan", requirementsMarkdown = "Files: conanfile.txt or conanfile.py. <br /><br /> Executable: conan.")
public class ConanCliDetectable extends Detectable {
    public static final String CONANFILETXT = "conanfile.txt";
    public static final String CONANFILEPY = "conanfile.py";
    private final FileFinder fileFinder;
    private final ConanResolver conanResolver;
    private final ConanCliExtractor conanCliExtractor;
    private final ConanCliExtractorOptions conanCliExtractorOptions;

    private File conanFile;
    private File conanExe;

    public ConanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanResolver conanResolver, ConanCliExtractor conanCliExtractor,
        ConanCliExtractorOptions conanCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanResolver = conanResolver;
        this.conanCliExtractor = conanCliExtractor;
        this.conanCliExtractorOptions = conanCliExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        File conanTxtFile = fileFinder.findFile(environment.getDirectory(), CONANFILETXT);
        if (conanTxtFile == null) {
            File conanPyFile = fileFinder.findFile(environment.getDirectory(), CONANFILEPY);
            if (conanPyFile == null) {
                return new FileNotFoundDetectableResult(CONANFILETXT);
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        conanExe = conanResolver.resolveConan(environment);

        if (conanExe == null) {
            return new ExecutableNotFoundDetectableResult("conan");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return conanCliExtractor.extract(environment.getDirectory(), conanExe, conanCliExtractorOptions);
    }
}
