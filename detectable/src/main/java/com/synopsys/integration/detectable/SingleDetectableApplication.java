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

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.factory.DetectableFactory;
import com.synopsys.integration.detectable.detectable.factory.ExtractorFactory;
import com.synopsys.integration.detectable.detectable.factory.UtilityFactory;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;

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
        final BitbakeDetectableOptions options = new BitbakeDetectableOptions("", new String[] { "" });

        //Objects
        final BitbakeDetectable bitbakeDetectable = detectableFactory.bitbakeDetectable(environment, options);

        //Extraction
        try {
            if (bitbakeDetectable.applicable().getPassed()) {
                if (bitbakeDetectable.extractable().getPassed()) {
                    return bitbakeDetectable.extract(new ExtractionEnvironment(outputDirectory));
                }
            }
        } catch (final DetectableException exception) {
            return null;
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
        final ExecutableResolver cachedExecutableResolver = utilityFactory.cachedExecutableResolver();
        final BitbakeExtractor bitbakeExtractor = extractorFactory.bitbakeExtractor();

        //Search
        final File bitbakeFile = simpleFileFinder.findFile(sourceDirectory, "*.bitbake");//TODO: bitbake is a terrible example
        final File bashExecutable = cachedExecutableResolver.resolveExecutable(ExecutableType.BASH, new DetectableEnvironment(sourceDirectory));

        //Extraction
        return bitbakeExtractor.extract(new ExtractionEnvironment(outputDirectory), bitbakeFile, sourceDirectory, new String[] { "" }, bashExecutable);
    }
}
