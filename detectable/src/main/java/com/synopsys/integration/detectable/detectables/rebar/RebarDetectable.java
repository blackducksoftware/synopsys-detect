package com.synopsys.integration.detectable.detectables.rebar;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Rebar CLI", language = "Erlang", forge = "Hex", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: rebar.config. Executable: rebar3.")
public class RebarDetectable extends Detectable {
    public static final String REBAR_CONFIG = "rebar.config";

    private final FileFinder fileFinder;
    private final Rebar3Resolver rebar3Resolver;
    private final RebarExtractor rebarExtractor;

    private ExecutableTarget rebarExe;

    public RebarDetectable(DetectableEnvironment environment, FileFinder fileFinder, Rebar3Resolver rebar3Resolver, RebarExtractor rebarExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.rebarExtractor = rebarExtractor;
        this.rebar3Resolver = rebar3Resolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(REBAR_CONFIG);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        rebarExe = requirements.executable(rebar3Resolver::resolveRebar3, "rebar3");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return rebarExtractor.extract(environment.getDirectory(), rebarExe);
    }

}
