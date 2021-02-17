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
package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Perl", forge = "CPAN", requirementsMarkdown = "File: Makefile.PL. <br /><br /> Executable: cpan.")
public class CpanCliDetectable extends Detectable {
    private static final String MAKEFILE = "Makefile.PL";

    private final FileFinder fileFinder;
    private final CpanResolver cpanResolver;
    private final CpanmResolver cpanmResolver;
    private final CpanCliExtractor cpanCliExtractor;

    private ExecutableTarget cpanExe;
    private ExecutableTarget cpanmExe;

    public CpanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, CpanResolver cpanResolver, CpanmResolver cpanmResolver, CpanCliExtractor cpanCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.cpanResolver = cpanResolver;
        this.cpanmResolver = cpanmResolver;
        this.cpanCliExtractor = cpanCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(MAKEFILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        cpanExe = requirements.executable(cpanResolver::resolveCpan, "cpan");
        cpanmExe = requirements.executable(cpanmResolver::resolveCpanm, "cpanm");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return cpanCliExtractor.extract(cpanExe, cpanmExe, extractionEnvironment.getOutputDirectory());
    }

}

