package com.blackduck.integration.detectable.detectables.sbt;

import java.util.ArrayList;
import java.util.List;

import com.blackduck.integration.detectable.detectable.executable.resolver.SbtResolver;
import com.blackduck.integration.detectable.detectables.sbt.dot.SbtDotExtractor;
import com.blackduck.integration.detectable.detectables.sbt.dot.SbtPluginFinder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.explanation.Explanation;
import com.blackduck.integration.detectable.detectable.explanation.FoundExecutable;
import com.blackduck.integration.detectable.detectable.explanation.FoundSbtPlugin;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.detectable.result.SbtMissingPluginDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

//Even though this is technically two differenct extractors it's been combined because one of the approaches is deprecated. In the future only the plugin approach will be taken.
@DetectableInfo(name = "Sbt Native Inspector", language = "Scala", forge = "Maven Central", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: build.sbt. Plugin: Dependency Graph")
public class SbtDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final FileFinder fileFinder;
    @Nullable
    private final String sbtCommandAdditionalArguments;
    private final SbtResolver sbtResolver;
    private final SbtDotExtractor sbtPluginExtractor;
    private final SbtPluginFinder sbtPluginFinder;

    private ExecutableTarget sbt;
    private boolean foundPlugin;

    public SbtDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        String sbtCommandAdditionalArguments,
        SbtResolver sbtResolver,
        SbtDotExtractor sbtPluginExtractor,
        SbtPluginFinder sbtPluginFinder
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.sbtCommandAdditionalArguments = sbtCommandAdditionalArguments;
        this.sbtResolver = sbtResolver;
        this.sbtPluginExtractor = sbtPluginExtractor;
        this.sbtPluginFinder = sbtPluginFinder;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(BUILD_SBT_FILENAME);
        return requirements.result();
    }

    //Check if SBT & a plugin can be found
    @Override
    public DetectableResult extractable() throws DetectableException {
        List<Explanation> explanations = new ArrayList<>();
        sbt = sbtResolver.resolveSbt();
        if (sbt == null) {
            return new ExecutableNotFoundDetectableResult("sbt");
        } else {
            explanations.add(new FoundExecutable(sbt));
        }

        foundPlugin = sbtPluginFinder.isPluginInstalled(environment.getDirectory(), sbt, sbtCommandAdditionalArguments);
        if (!foundPlugin) {
            return new SbtMissingPluginDetectableResult(environment.getDirectory().toString());
        } else {
            explanations.add(new FoundSbtPlugin("Dependency Graph"));
        }

        return new PassedDetectableResult(explanations);
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        if (sbt != null && foundPlugin) {
            return sbtPluginExtractor.extract(environment.getDirectory(), sbt, sbtCommandAdditionalArguments);
        } else {
            return new Extraction.Builder().failure("No SBT plugin was found.  Please install necessary SBT plugin.")
                .build();
        }
    }

}
