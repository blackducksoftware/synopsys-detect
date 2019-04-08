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
package com.synopsys.integration.detectable.detectables.maven.parsing.parse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PomDependenciesHandler extends DefaultHandler {
    private final ExternalIdFactory externalIdFactory;

    private boolean parsingDependencies;
    private boolean parsingDependency;
    private boolean parsingGroup;
    private boolean parsingArtifact;
    private boolean parsingVersion;

    private String group;
    private String artifact;
    private String version;

    private final List<Dependency> dependencies = new ArrayList<>();

    public PomDependenciesHandler(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("dependencies".equals(qName)) {
            parsingDependencies = true;
        } else if (parsingDependencies && "dependency".equals(qName)) {
            parsingDependency = true;
        } else if (parsingDependency && "groupId".equals(qName)) {
            parsingGroup();
        } else if (parsingDependency && "artifactId".equals(qName)) {
            parsingArtifact();
        } else if (parsingDependency && "version".equals(qName)) {
            parsingVersion();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("dependencies".equals(qName)) {
            parsingDependencies = false;
        } else if ("dependency".equals(qName)) {
            parsingDependency = false;

            final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
            dependencies.add(new Dependency(artifact, version, externalId));
        } else {
            parsingNothingImportant();
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        if (parsingGroup) {
            group = new String(ch, start, length);
        } else if (parsingArtifact) {
            artifact = new String(ch, start, length);
        } else if (parsingVersion) {
            version = new String(ch, start, length);
        }
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    private void parsingNothingImportant() {
        parsingGroup = false;
        parsingArtifact = false;
        parsingVersion = false;
    }

    private void parsingGroup() {
        parsingNothingImportant();
        parsingGroup = true;
    }

    private void parsingArtifact() {
        parsingNothingImportant();
        parsingArtifact = true;
    }

    private void parsingVersion() {
        parsingNothingImportant();
        parsingVersion = true;
    }
}
