package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.explanation.PropertyProvided;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Bazel CLI", language = "various", forge = "Maven Central", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: WORKSPACE. Executable: bazel.")
public class BazelDetectable extends Detectable {
    public static final String WORKSPACE_FILENAME = "WORKSPACE";
    private final FileFinder fileFinder;
    private final BazelExtractor bazelExtractor;
    private final BazelResolver bazelResolver;
    private final String bazelTargetName;
    private ExecutableTarget bazelExe;
    private File workspaceFile;

    public BazelDetectable(DetectableEnvironment environment, FileFinder fileFinder, BazelExtractor bazelExtractor, BazelResolver bazelResolver, @Nullable String bazelTargetName) {
        super(environment);
        this.fileFinder = fileFinder;
        this.bazelExtractor = bazelExtractor;
        this.bazelResolver = bazelResolver;
        this.bazelTargetName = bazelTargetName;
    }

    @Override
    public DetectableResult applicable() {
        if (bazelTargetName != null) {
            return new PassedDetectableResult(new PropertyProvided("Bazel Target"));
        } else {
            return new PropertyInsufficientDetectableResult();
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        workspaceFile = requirements.file(WORKSPACE_FILENAME);
        bazelExe = requirements.executable(bazelResolver::resolveBazel, "bazel");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws DetectableException, ExecutableFailedException {
        return bazelExtractor
            .extract(bazelExe, environment.getDirectory(), workspaceFile);
    }
}
