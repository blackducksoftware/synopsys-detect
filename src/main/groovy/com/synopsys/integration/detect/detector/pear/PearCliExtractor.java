/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.pear;

import java.io.File;

import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.util.executable.ExecutableOutput;
import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationType;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PearCliExtractor {
    private static final String PACKAGE_XML_FILENAME = "package.xml";

    private final DetectFileFinder detectFileFinder;
    private final ExternalIdFactory externalIdFactory;
    private final PearParser pearParser;
    private final ExecutableRunner executableRunner;
    private final DirectoryManager directoryManager;

    public PearCliExtractor(final DetectFileFinder detectFileFinder, final ExternalIdFactory externalIdFactory, final PearParser pearParser, final ExecutableRunner executableRunner, DirectoryManager directoryManager) {
        this.detectFileFinder = detectFileFinder;
        this.externalIdFactory = externalIdFactory;
        this.pearParser = pearParser;
        this.executableRunner = executableRunner;
        this.directoryManager = directoryManager;
    }

    public Extraction extract(final File directory, final File pearExe, final ExtractionId extractionId) {
        try {
            File workingDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
            final ExecutableOutput pearListing = executableRunner.execute(workingDirectory, pearExe, "list");
            final ExecutableOutput pearDependencies = executableRunner.execute(workingDirectory, pearExe, "package-dependencies", PACKAGE_XML_FILENAME);

            final File packageFile = detectFileFinder.findFile(directory, PACKAGE_XML_FILENAME); //TODO: Why is this done here?

            final PearParseResult result = pearParser.parse(packageFile, pearListing, pearDependencies);
            final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PEAR, result.name, result.version);
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.PEAR, directory.toString(), id, result.dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).projectName(result.name).projectVersion(result.version).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
