package com.synopsys.integration.detectable.detectables.opam.buildexe;

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
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.executable.ExecutableRunnerException;

import java.io.File;
import java.util.List;


@DetectableInfo(name = "Opam CLI", language = "OCaml", forge = "opam", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "A .opam file and the opam executable.")
public class OpamBuildDetectable extends Detectable {

    private static final String OPAM_FILE = "*.opam";
    private final FileFinder fileFinder;
    private final OpamResolver opamResolver;
    private final OpamBuildExtractor opamBuildExtractor;

    private ExecutableTarget opamExe;

    public OpamBuildDetectable(DetectableEnvironment environment, FileFinder fileFinder, OpamResolver opamResolver, OpamBuildExtractor opamBuildExtractor){
        super(environment);
        this.fileFinder = fileFinder;
        this.opamResolver = opamResolver;
        this.opamBuildExtractor = opamBuildExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(OPAM_FILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        opamExe = opamResolver.resolveOpam();

        if(opamExe == null) {
            return new ExecutableNotFoundDetectableResult("opam");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException {
        List<File> opamFiles = fileFinder.findFiles(environment.getDirectory(),OPAM_FILE);
        return opamBuildExtractor.extract(opamFiles, opamExe);
    }

}
