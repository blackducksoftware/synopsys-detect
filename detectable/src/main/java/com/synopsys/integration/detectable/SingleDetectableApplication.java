/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;
import com.synopsys.integration.detectable.detectables.npm.NpmPackageJsonDiscoverer;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.factory.DetectableFactory;

//This sample application will an example detectable tool and execute it against the current folder.
public class SingleDetectableApplication {
    public static void main(final String[] args) {
        /*
         * Empty main method
         */
    }

    //In this example, we use the Detectable to determine if we can extract and if all necessary pieces are present.
    public Extraction DetectableExample() throws DetectableException {
        //Factory
        final SimpleFileFinder simpleFileFinder = new SimpleFileFinder();
        final ExecutableRunner executableRunner = new SimpleExecutableRunner();
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final DetectableFactory detectableFactory = new DetectableFactory(simpleFileFinder, executableRunner, externalIdFactory, gson);

        //Data
        final File sourceDirectory = new File("");
        final File outputDirectory = new File("");
        final DetectableEnvironment environment = new DetectableEnvironment(sourceDirectory);
        final NpmCliExtractorOptions npmCliExtractorOptions = new NpmCliExtractorOptions(true, null);

        //Objects
        SimpleExecutableFinder simpleExecutableFinder = SimpleExecutableFinder.forCurrentOperatingSystem(simpleFileFinder);
        final NpmResolver npmResolver = new SimpleExecutableResolver(new CachedExecutableResolverOptions(false), new SimpleLocalExecutableFinder(simpleExecutableFinder), new SimpleSystemExecutableFinder(simpleExecutableFinder));
        final NpmCliParser npmCliParser = new NpmCliParser(new ExternalIdFactory());
        final NpmCliExtractor npmCliExtractor = new NpmCliExtractor(executableRunner, npmCliParser);
        final NpmPackageJsonDiscoverer npmPackageJsonDiscoverer = new NpmPackageJsonDiscoverer(new Gson());
        final NpmCliDetectable npmCliDetectable = new NpmCliDetectable(environment, simpleFileFinder, npmResolver, npmCliExtractor, npmPackageJsonDiscoverer, new NpmCliExtractorOptions(true, ""));

        //Extraction
        if (npmCliDetectable.applicable().getPassed()) {
            if (npmCliDetectable.extractable().getPassed()) {
                return npmCliDetectable.extract(new ExtractionEnvironment(outputDirectory));
            }
        }
        return null;
    }
}
