/*
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
package com.synopsys.integration.detectable.detectables.rebar;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Erlang", forge = "Hex", requirementsMarkdown = "File: rebar.config.<br/><br/>Executable: rebar3.")
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
