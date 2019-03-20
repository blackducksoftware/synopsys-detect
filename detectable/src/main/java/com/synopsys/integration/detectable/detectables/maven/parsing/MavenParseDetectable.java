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
package com.synopsys.integration.detectable.detectables.maven.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.maven.parsing.parse.PomXmlParser;

public class MavenParseDetectable extends Detectable {
    public static final String POM_XML_FILENAME = "pom.xml";

    private final FileFinder fileFinder;
    private final PomXmlParser pomXmlParser;

    private File pomXmlFile;

    public MavenParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PomXmlParser pomXmlParser) {
        super(environment, "pom.xml", "MAVEN");
        this.fileFinder = fileFinder;
        this.pomXmlParser = pomXmlParser;
    }

    @Override
    public DetectableResult applicable() {
        pomXmlFile = fileFinder.findFile(environment.getDirectory(), POM_XML_FILENAME);

        if (pomXmlFile == null) {
            return new FileNotFoundDetectableResult(POM_XML_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream pomXmlInputStream = new FileInputStream(pomXmlFile)) {
            final Optional<DependencyGraph> dependencyGraph = pomXmlParser.parse(pomXmlInputStream);

            if (dependencyGraph.isPresent()) {
                final CodeLocation codeLocation = new CodeLocation(dependencyGraph.get());
                return new Extraction.Builder().success(codeLocation).build();
            } else {
                return new Extraction.Builder().failure(String.format("Failed to extract dependencies from %s", POM_XML_FILENAME)).build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
