package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Clang CLI", language = "C or C++", forge = "Derived from the Linux distribution.", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: compile_commands.json. Executable: Linux package manager.")
public class ClangDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String JSON_COMPILATION_DATABASE_FILENAME = "compile_commands.json";
    private final ClangExtractor clangExtractor;
    private final ClangDetectableOptions options;
    private File jsonCompilationDatabaseFile = null;
    private final FileFinder fileFinder;
    private final DetectableExecutableRunner executableRunner;
    private final List<ClangPackageManager> availablePackageManagers;
    private final ClangPackageManagerRunner packageManagerRunner;

    private ClangPackageManager selectedPackageManager;

    public ClangDetectable(
        DetectableEnvironment environment,
        DetectableExecutableRunner executableRunner,
        FileFinder fileFinder,
        List<ClangPackageManager> availablePackageManagers,
        ClangExtractor clangExtractor,
        ClangDetectableOptions options,
        ClangPackageManagerRunner packageManagerRunner
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.availablePackageManagers = availablePackageManagers;
        this.executableRunner = executableRunner;
        this.clangExtractor = clangExtractor;
        this.options = options;
        this.packageManagerRunner = packageManagerRunner;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        jsonCompilationDatabaseFile = requirements.file(JSON_COMPILATION_DATABASE_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        selectedPackageManager = findPkgMgr(environment.getDirectory());
        if (selectedPackageManager == null) {
            logger.warn("Unable to execute any supported package manager; Please make sure that one of the supported clang package managers is on the PATH");
            return new ExecutableNotFoundDetectableResult("supported Linux package manager");
        }
        return new PassedDetectableResult(new FoundExecutable(selectedPackageManager.getPackageManagerInfo().getPkgMgrName()));
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return clangExtractor.extract(
            selectedPackageManager,
            packageManagerRunner,
            environment.getDirectory(),
            extractionEnvironment.getOutputDirectory(),
            jsonCompilationDatabaseFile,
            options.isCleanup()
        );
    }

    private ClangPackageManager findPkgMgr(File workingDirectory) {
        for (ClangPackageManager pkgMgrCandidate : availablePackageManagers) {
            if (packageManagerRunner.applies(pkgMgrCandidate, workingDirectory, executableRunner)) {
                return pkgMgrCandidate;
            }
        }
        return null;
    }
}
