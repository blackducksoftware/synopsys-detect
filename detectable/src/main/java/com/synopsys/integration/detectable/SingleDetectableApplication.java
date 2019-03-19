/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.factory.DetectableFactory;
import com.synopsys.integration.detectable.detectable.factory.ExtractorFactory;
import com.synopsys.integration.detectable.detectable.factory.UtilityFactory;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;

//This sample application will an example detectable tool and execute it against the current folder.
public class SingleDetectableApplication {
    public static void main(final String[] args) {

    }

    //In this example, we use the Detectable to determine if we can extract and if all necessary pieces are present.
    public Extraction DetectableExample() {
        //Factory
        final UtilityFactory utilityFactory = new UtilityFactory();
        final ExtractorFactory extractorFactory = new ExtractorFactory(utilityFactory);
        final DetectableFactory detectableFactory = new DetectableFactory(utilityFactory, extractorFactory);

        //Data
        final File sourceDirectory = new File("");
        final File outputDirectory = new File("");
        final DetectableEnvironment environment = new DetectableEnvironment(sourceDirectory);
        final NpmCliExtractorOptions npmCliExtractorOptions = new NpmCliExtractorOptions(true, null);

        //Objects
        final FileFinder simpleFileFinder = utilityFactory.simpleFileFinder();
        final NpmResolver npmResolver = utilityFactory.executableResolver();
        final ExecutableRunner executableRunner = new SimpleExecutableRunner();
        final NpmCliParser npmCliParser = new NpmCliParser(new ExternalIdFactory());
        final NpmCliExtractor npmCliExtractor = new NpmCliExtractor(executableRunner, npmCliParser, npmCliExtractorOptions);
        final NpmCliDetectable npmCliDetectable = new NpmCliDetectable(environment, simpleFileFinder, npmResolver, npmCliExtractor);

        //Extraction
        if (npmCliDetectable.applicable().getPassed()) {
            if (npmCliDetectable.extractable().getPassed()) {
                return npmCliDetectable.extract(new ExtractionEnvironment(outputDirectory));
            }
        }
        return null;
    }

    //In this example, we use the factory to create the objects but will manually look for the files and perform the extraction.
    public Extraction ExtractorExample() throws DetectableException {
        //Factory
        final UtilityFactory utilityFactory = new UtilityFactory();
        final ExtractorFactory extractorFactory = new ExtractorFactory(utilityFactory);

        //Data
        final File sourceDirectory = new File("");
        final File outputDirectory = new File("");

        //Objects
        final FileFinder simpleFileFinder = utilityFactory.simpleFileFinder();
        final SimpleExecutableResolver executableResolver = utilityFactory.executableResolver();
        final BitbakeExtractor bitbakeExtractor = extractorFactory.bitbakeExtractor();

        //Search
        final File bitbakeFile = simpleFileFinder.findFile(sourceDirectory, "oe-init-build-env");
        final File bashExecutable = executableResolver.resolveBash();

        //Extraction
        return bitbakeExtractor.extract(new ExtractionEnvironment(outputDirectory), bitbakeFile, sourceDirectory, new String[] { "" }, bashExecutable, "");
    }
}
