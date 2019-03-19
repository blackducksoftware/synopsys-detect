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
package com.synopsys.integration.detectable.detectables.pear;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageXmlParser;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PearCliExtractor {
    private static final String PACKAGE_XML_FILENAME = "package.xml";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final PearDependencyGraphTransformer pearDependencyGraphTransformer;
    private final PearPackageXmlParser pearPackageXmlParser;
    private final PearPackageDependenciesParser pearPackageDependenciesParser;
    private final PearListParser pearListParser;

    public PearCliExtractor(final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner, final PearDependencyGraphTransformer pearDependencyGraphTransformer, final PearPackageXmlParser pearPackageXmlParser,
        final PearPackageDependenciesParser pearPackageDependenciesParser, final PearListParser pearListParser) {
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.pearDependencyGraphTransformer = pearDependencyGraphTransformer;
        this.pearPackageXmlParser = pearPackageXmlParser;
        this.pearPackageDependenciesParser = pearPackageDependenciesParser;
        this.pearListParser = pearListParser;
    }

    public Extraction extract(final File pearExe, final File packageXmlFile, final File extractionDirectory, final boolean onlyGatherRequired) {
        try {
            final ExecutableOutput pearListOutput = executableRunner.execute(extractionDirectory, pearExe, "list");
            final ExecutableOutput packageDependenciesOutput = executableRunner.execute(extractionDirectory, pearExe, "package-dependencies", PACKAGE_XML_FILENAME);
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
        if (StringUtils.isNotBlank(pearDependencies.getErrorOutput()) || StringUtils.isNotBlank(pearListing.getErrorOutput())) {
            logger.error("There was an error during execution.");
            if (StringUtils.isNotBlank(pearListing.getErrorOutput())) {
                logger.error("Pear list error: ");
                logger.error(pearListing.getErrorOutput());
            }
            if (StringUtils.isNotBlank(pearDependencies.getErrorOutput())) {
                logger.error("Pear package-dependencies error: ");
                logger.error(pearDependencies.getErrorOutput());
            }

            throw new IntegrationException("There was an error during execution of the pear CLI");
        } else if (!(pearDependencies.getStandardOutputAsList().size() > 0) || !(pearListing.getStandardOutputAsList().size() > 0)) {
            throw new IntegrationException("No information retrieved from running pear commands");
        }
    }
}
