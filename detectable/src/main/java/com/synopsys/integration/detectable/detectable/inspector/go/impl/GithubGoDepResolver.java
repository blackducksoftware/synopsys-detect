/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectable.inspector.go.impl;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.inspector.go.GoDepResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;

public class GithubGoDepResolver implements GoDepResolver, GoResolver {
    private final Logger logger = LoggerFactory.getLogger(GithubGoDepResolver.class);

    private final ExecutableRunner executableRunner;
    private final SimpleLocalExecutableFinder simpleLocalExecutableFinder;
    private final SimpleSystemExecutableFinder simpleSystemExecutableFinder;
    private final File downloadDirectory;

    public GithubGoDepResolver(final ExecutableRunner executableRunner, final SimpleLocalExecutableFinder simpleLocalExecutableFinder, final SimpleSystemExecutableFinder simpleSystemExecutableFinder, final File downloadDirectory) {
        this.executableRunner = executableRunner;
        this.simpleLocalExecutableFinder = simpleLocalExecutableFinder;
        this.simpleSystemExecutableFinder = simpleSystemExecutableFinder;
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public File resolveGoDep(final File location) throws DetectableException {
        File goDep = simpleLocalExecutableFinder.findExecutable("dep", location);

        if (goDep == null) {
            final File go = resolveGo();
            try {
                goDep = installGoDep(go);
            } catch (final ExecutableRunnerException e) {
                throw new DetectableException("Failed to install go dep.", e);
            }
        }
        return goDep;
    }

    @Override
    public File resolveGo() {
        return simpleSystemExecutableFinder.findExecutable("go");
    }

    private File installGoDep(final File goExecutable) throws ExecutableRunnerException {
        final File goDep = new File(downloadDirectory, "dep");
        final File installDirectory = goDep.getParentFile();
        installDirectory.mkdirs();

        logger.debug("Retrieving the Go Dep tool");

        executableRunner.execute(installDirectory, goExecutable, Arrays.asList(
            "get",
            "-u",
            "-v",
            "-d",
            "github.com/golang/dep/cmd/dep"));

        logger.debug("Building the Go Dep tool in " + installDirectory.getAbsolutePath());

        executableRunner.execute(installDirectory, goExecutable, Arrays.asList(
            "build",
            "github.com/golang/dep/cmd/dep"));

        return goDep;
    }
}
