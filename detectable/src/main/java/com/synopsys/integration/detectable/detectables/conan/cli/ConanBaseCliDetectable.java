package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.WrongConanExecutableVersionResult;

public abstract class ConanBaseCliDetectable extends Detectable {
    public static final String CONANFILETXT = "conanfile.txt";
    public static final String CONANFILEPY = "conanfile.py";

    protected ExecutableTarget conanExe;
    protected final ConanCliExtractor conanCliExtractor;

    private final FileFinder fileFinder;
    private final ConanResolver conanResolver;

    public ConanBaseCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanResolver conanResolver, ConanCliExtractor conanCliExtractor) {
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
        conanExe = conanResolver.resolveConan(environment);

        String expectedVersion = getExpectedMajorConanVersion();
        String actualVersion;

        try {
            actualVersion = conanCliExtractor.extractConanMajorVersion(environment.getDirectory(), conanExe);
        } catch (Exception e) {
            return new ExceptionDetectableResult(e);
        }

        if (!expectedVersion.equals(actualVersion)) {
            return new WrongConanExecutableVersionResult(expectedVersion, actualVersion);
        }
        return new PassedDetectableResult();
    }

    abstract protected String getExpectedMajorConanVersion();
}
