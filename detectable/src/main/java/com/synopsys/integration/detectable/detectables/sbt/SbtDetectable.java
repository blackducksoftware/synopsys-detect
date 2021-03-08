/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.SbtResolver;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundSbtPlugin;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SbtMissingPluginDetectableResult;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotExtractor;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtPluginFinder;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheExtractor;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

//Even though this is technically two differenct extractors it's been combined because one of the approaches is deprecated. In the future only the plugin approach will be taken.
@DetectableInfo(language = "Scala", forge = "Maven Central", requirementsMarkdown = "File: build.sbt. Plugin: Dependency Graph")
public class SbtDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final FileFinder fileFinder;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;
    private final SbtResolutionCacheOptions sbtResolutionCacheOptions;
    private final SbtResolver sbtResolver;
    private final SbtDotExtractor sbtPluginExtractor;
    private final SbtPluginFinder sbtPluginFinder;

    private ExecutableTarget sbt;
    private boolean foundPlugin;

    public SbtDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final SbtResolutionCacheExtractor sbtResolutionCacheExtractor,
        SbtResolutionCacheOptions sbtResolutionCacheOptions, final SbtResolver sbtResolver, final SbtDotExtractor sbtPluginExtractor,
        final SbtPluginFinder sbtPluginFinder) {
        super(environment);
        this.fileFinder = fileFinder;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
        this.sbtResolutionCacheOptions = sbtResolutionCacheOptions;
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

    @Override
    public DetectableResult extractable() throws DetectableException {
        DetectableResult pluginResult = sbtPluginExtractable();
        if (pluginResult.getPassed()) {
            return pluginResult;
        } else {
            return new PassedDetectableResult();
        }
    }

    //Check if SBT & a plugin can be found
    private DetectableResult sbtPluginExtractable() throws DetectableException {
        List<Explanation> explanations = new ArrayList<>();
        sbt = sbtResolver.resolveSbt();
        if (sbt == null) {
            return new ExecutableNotFoundDetectableResult("sbt");
        } else {
            explanations.add(new FoundExecutable(sbt));
        }

        foundPlugin = sbtPluginFinder.isPluginInstalled(environment.getDirectory(), sbt);
        if (!foundPlugin) {
            return new SbtMissingPluginDetectableResult(environment.getDirectory().toString());
        } else {
            explanations.add(new FoundSbtPlugin("Dependency Graph"));
        }

        return new PassedDetectableResult(explanations);
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        if (sbt != null && foundPlugin) {
            if (sbtResolutionCacheOptions.getExcludedConfigurations().size() > 0 || sbtResolutionCacheOptions.getIncludedConfigurations().size() > 0) {
                return new Extraction.Builder().failure(
                    "Included and excluded SBT configurations can not be used when an sbt plugin is used for dependency resolution. They can still be used when not using a dependency plugin, either remove the plugin or do not provide the properties.")
                           .build();
            }
            return sbtPluginExtractor.extract(environment.getDirectory(), sbt);
        } else {
            logger.warn("No SBT plugin was found, will attempt to parse report files. This approach is deprecated and a plugin should be installed. ");
            return sbtResolutionCacheExtractor.extract(environment.getDirectory(), sbtResolutionCacheOptions);
        }
    }

}
