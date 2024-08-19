package com.synopsys.integration.detectable.detectables.opam.lockfile;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.OpamResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.opam.build.OpamBuildExtractor;
import com.synopsys.integration.detectable.detectables.opam.lockfile.parse.OpamLockFileParser;
import com.synopsys.integration.detectable.detectables.opam.parse.OpamFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.executable.ExecutableRunnerException;

import java.io.File;
import java.util.List;

@DetectableInfo(name = "Opam Lock File", language = "OCaml", forge = "opam", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "A .opam file and a .opam.locked file")
public class OpamLockFileDetectable extends Detectable {

    private static final String OPAM_FILE = "*.opam";
    private static final String OPAM_LOCKED_FILE = "*.opam.locked";
    private final FileFinder fileFinder;
    private final OpamLockFileExtractor opamLockFileExtractor;


    public OpamLockFileDetectable(DetectableEnvironment environment, FileFinder fileFinder, OpamLockFileExtractor opamLockFileExtractor){
        super(environment);
        this.fileFinder = fileFinder;
        this.opamLockFileExtractor = opamLockFileExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(OPAM_LOCKED_FILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(OPAM_FILE);
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException {
        List<File> opamFiles = fileFinder.findFiles(environment.getDirectory(),OPAM_FILE);
        String projectName = environment.getDirectory().getName();

        List<File> opamLockFiles = fileFinder.findFiles(environment.getDirectory(), OPAM_LOCKED_FILE);
        OpamLockFileParser opamLockFileParser = new OpamLockFileParser(opamLockFiles);

        return opamLockFileExtractor.extract(opamFiles, projectName, opamLockFileParser);
    }

}
