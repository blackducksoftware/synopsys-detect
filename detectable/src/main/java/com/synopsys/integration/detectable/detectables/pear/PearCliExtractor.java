/**
 * hub-detect
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
package com.synopsys.integration.detectable.detectables.pear;

import java.io.File;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;

public class PearCliExtractor {
    private static final String PACKAGE_XML_FILENAME = "package.xml";

    private final ExternalIdFactory externalIdFactory;
    private final PearParser pearParser;
    private final ExecutableRunner executableRunner;
    private final PearCliDetectableOptions pearCliDetectableOptions;

    public PearCliExtractor(final ExternalIdFactory externalIdFactory, final PearParser pearParser, final ExecutableRunner executableRunner, final PearCliDetectableOptions pearCliDetectableOptions) {
        this.externalIdFactory = externalIdFactory;
        this.pearParser = pearParser;
        this.executableRunner = executableRunner;
        this.pearCliDetectableOptions = pearCliDetectableOptions;
    }

    public Extraction extract(final File pearExe, final File packageXmlFile, final File extractionDirectory) {
        try {
            final ExecutableOutput pearListing = executableRunner.execute(extractionDirectory, pearExe, "list");
            final ExecutableOutput pearDependencies = executableRunner.execute(extractionDirectory, pearExe, "package-dependencies", PACKAGE_XML_FILENAME);

            final PearParseResult result = pearParser.parse(packageXmlFile, pearListing, pearDependencies, pearCliDetectableOptions.onlyGatherRequired());
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, result.name, result.version);
            final CodeLocation detectCodeLocation = new CodeLocation.Builder(CodeLocationType.PEAR, result.dependencyGraph, externalId).build();

            return new Extraction.Builder().success(detectCodeLocation).projectName(result.name).projectVersion(result.version).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
