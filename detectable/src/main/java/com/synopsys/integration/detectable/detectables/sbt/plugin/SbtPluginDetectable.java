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
package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.util.ArrayList;
import java.util.List;

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
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Scala", forge = "Maven Central", requirementsMarkdown = "File: build.sbt.")
public class SbtPluginDetectable extends Detectable {
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final FileFinder fileFinder;
    private final SbtResolver sbtResolver;
    private final SbtPluginExtractor sbtPluginExtractor;
    private final SbtPluginFinder sbtPluginFinder;

    private ExecutableTarget sbt;
    private SbtPlugin plugin;

    public SbtPluginDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final SbtResolver sbtResolver, final SbtPluginExtractor sbtPluginExtractor,
        final SbtPluginFinder sbtPluginFinder) {
        super(environment);
        this.fileFinder = fileFinder;
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
        return sbtPluginExtractor.extract(environment.getDirectory(), sbt, plugin);
    }

}
