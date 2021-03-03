/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse;

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
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SbtMissingPluginDetectableResult;
import com.synopsys.integration.detectable.detectables.sbt.plugin.SbtPlugin;
import com.synopsys.integration.detectable.detectables.sbt.plugin.SbtPluginExtractor;
import com.synopsys.integration.detectable.detectables.sbt.plugin.SbtPluginFinder;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

//Even though this is technically two differenct extractors it's been combined because one of the approaches is deprecated. In the future only the plugin approach will be taken.
@DetectableInfo(language = "Scala", forge = "Maven Central", requirementsMarkdown = "File: build.sbt.")
public class SbtDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final FileFinder fileFinder;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;
    private final SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions;
    private final SbtResolver sbtResolver;
    private final SbtPluginExtractor sbtPluginExtractor;
    private final SbtPluginFinder sbtPluginFinder;

    private ExecutableTarget sbt;
    private SbtPlugin plugin;

    public SbtDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final SbtResolutionCacheExtractor sbtResolutionCacheExtractor,
        SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions, final SbtResolver sbtResolver, final SbtPluginExtractor sbtPluginExtractor,
        final SbtPluginFinder sbtPluginFinder) {
        super(environment);
        this.fileFinder = fileFinder;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
        this.sbtResolutionCacheDetectableOptions = sbtResolutionCacheDetectableOptions;
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

        plugin = sbtPluginFinder.findPlugin(environment.getDirectory(), sbt);
        if (plugin == null) {
            return new SbtMissingPluginDetectableResult(environment.getDirectory().toString());
        } else {
            explanations.add(new FoundSbtPlugin(plugin.getName()));
        }

        return new PassedDetectableResult(explanations);
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        if (sbt != null && plugin != null) {
            if (sbtResolutionCacheDetectableOptions.getExcludedConfigurations().size() > 0 || sbtResolutionCacheDetectableOptions.getIncludedConfigurations().size() > 0) {
                return new Extraction.Builder().failure(
                    "Included and excluded SBT configurations can not be used when an sbt plugin is used for dependency resolution. They can still be used when not using a dependency plugin, either remove the plugin or do not provide the properties.")
                           .build();
            }
            return sbtPluginExtractor.extract(environment.getDirectory(), sbt, plugin);
        } else {
            logger.warn("No SBT plugin was found, will attempt to parse report files. This approach is deprecated and a plugin should be installed.");
            return sbtResolutionCacheExtractor.extract(environment.getDirectory(), sbtResolutionCacheDetectableOptions);
        }
    }

}
