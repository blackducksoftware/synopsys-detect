package com.blackduck.integration.detectable.detectables.bazel;

import java.io.File;

import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.explanation.PropertyProvided;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.detectable.executable.resolver.BazelResolver;

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
