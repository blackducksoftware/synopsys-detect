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
package com.synopsys.integration.detectable.detectables.git.cli;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "N/A", requirementsMarkdown = "Directory: .git. <br /><br /> Executable: git.")
public class GitCliDetectable extends Detectable {
    private static final String GIT_DIRECTORY_NAME = ".git";

    private final FileFinder fileFinder;
    private final GitCliExtractor gitCliExtractor;
    private final GitResolver gitResolver;

    private ExecutableTarget gitExecutable;

    public GitCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GitCliExtractor gitCliExtractor, final GitResolver gitResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gitCliExtractor = gitCliExtractor;
        this.gitResolver = gitResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requires = new Requirements(fileFinder, environment);
        requires.directory(GIT_DIRECTORY_NAME);
        return requires.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requires = new Requirements(fileFinder, environment);
        gitExecutable = requires.executable(gitResolver::resolveGit, "git");
        return requires.result();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return gitCliExtractor.extract(gitExecutable, environment.getDirectory());
    }

}
