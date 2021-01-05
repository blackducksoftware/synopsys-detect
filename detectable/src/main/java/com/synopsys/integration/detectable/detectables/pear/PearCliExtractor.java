/**
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
package com.synopsys.integration.detectable.detectables.pear;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageXmlParser;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class PearCliExtractor {
    private static final String PACKAGE_XML_FILENAME = "package.xml";

    private final ExternalIdFactory externalIdFactory;
    private final DetectableExecutableRunner executableRunner;
    private final PearDependencyGraphTransformer pearDependencyGraphTransformer;
    private final PearPackageXmlParser pearPackageXmlParser;
    private final PearPackageDependenciesParser pearPackageDependenciesParser;
    private final PearListParser pearListParser;

    public PearCliExtractor(final ExternalIdFactory externalIdFactory, final DetectableExecutableRunner executableRunner, final PearDependencyGraphTransformer pearDependencyGraphTransformer, final PearPackageXmlParser pearPackageXmlParser,
        final PearPackageDependenciesParser pearPackageDependenciesParser, final PearListParser pearListParser) {
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.pearDependencyGraphTransformer = pearDependencyGraphTransformer;
        this.pearPackageXmlParser = pearPackageXmlParser;
        this.pearPackageDependenciesParser = pearPackageDependenciesParser;
        this.pearListParser = pearListParser;
    }

    public Extraction extract(final File pearExe, final File packageXmlFile, final File workingDirectory, final boolean onlyGatherRequired) {
        try {
            final ExecutableOutput pearListOutput = executableRunner.execute(workingDirectory, pearExe, "list");
            final ExecutableOutput packageDependenciesOutput = executableRunner.execute(workingDirectory, pearExe, "package-dependencies", PACKAGE_XML_FILENAME);
            assertValidExecutableOutput(pearListOutput, packageDependenciesOutput);

            final Map<String, String> dependencyNameVersionMap = pearListParser.parse(pearListOutput.getStandardOutputAsList());
            final List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(packageDependenciesOutput.getStandardOutputAsList());
            final DependencyGraph dependencyGraph = pearDependencyGraphTransformer.buildDependencyGraph(dependencyNameVersionMap, packageDependencies, onlyGatherRequired);

            try (final InputStream packageXmlInputStream = new FileInputStream(packageXmlFile)) {
                final NameVersion projectNameVersion = pearPackageXmlParser.parse(packageXmlInputStream);

                final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, projectNameVersion.getName(), projectNameVersion.getVersion());
                final CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph, externalId);

                return new Extraction.Builder()
                           .success(detectCodeLocation)
                           .projectName(projectNameVersion.getName())
                           .projectVersion(projectNameVersion.getVersion())
                           .build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void assertValidExecutableOutput(final ExecutableOutput pearListing, final ExecutableOutput pearDependencies) throws IntegrationException {
        if (pearDependencies.getReturnCode() != 0 || StringUtils.isNotBlank(pearDependencies.getErrorOutput())) {
            throw new IntegrationException("Pear dependencies exit code must be 0 and have no error output.");
        } else if (pearListing.getReturnCode() != 0 || StringUtils.isNotBlank(pearListing.getErrorOutput())) {
            throw new IntegrationException("Pear listing exit code must be 0 and have no error output.");
        }

        if (StringUtils.isBlank(pearDependencies.getStandardOutput()) && StringUtils.isBlank(pearListing.getStandardOutput())) {
            throw new IntegrationException("No information retrieved from running pear commands");
        }
    }
}
