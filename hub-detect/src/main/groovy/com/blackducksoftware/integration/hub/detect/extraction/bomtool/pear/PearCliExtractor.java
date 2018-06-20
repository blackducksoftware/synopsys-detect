/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear.parse.PearDependencyFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear.parse.PearParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PearCliExtractor {

    static final String PACKAGE_XML_FILENAME = "package.xml";

    @Autowired
    protected DetectFileFinder detectFileFinder;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    PearDependencyFinder pearDependencyFinder;

    @Autowired
    protected ExecutableRunner executableRunner;

    public Extraction extract(final File directory, final File pearExe) {
        try {
            final ExecutableOutput pearListing = executableRunner.runExe(pearExe, "list");
            final ExecutableOutput pearDependencies = executableRunner.runExe(pearExe, "package-dependencies", PACKAGE_XML_FILENAME);

            final File packageFile = detectFileFinder.findFile(directory, PACKAGE_XML_FILENAME);

            final PearParseResult result = pearDependencyFinder.parse(packageFile, pearListing, pearDependencies);
            final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PEAR, result.name, result.version);
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.PEAR, directory.toString(), id, result.dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).projectName(result.name).projectVersion(result.version).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
