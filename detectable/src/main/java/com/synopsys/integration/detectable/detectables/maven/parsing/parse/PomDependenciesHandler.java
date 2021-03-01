/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.parsing.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PomDependenciesHandler extends DefaultHandler {
    private static final Set<String> ONLY_DEPENDENCIES = new HashSet<>(Collections.singletonList("dependency"));
    private static final Set<String> WITH_PLUGINS = new HashSet<>(Arrays.asList("dependency", "plugin"));

    private final ExternalIdFactory externalIdFactory;
    private final boolean includePluginDependencies;

    private boolean parsingDependency;
    private boolean parsingGroup;
    private boolean parsingArtifact;
    private boolean parsingVersion;

    private String group;
    private String artifact;
    private String version;

    private final List<Dependency> dependencies = new ArrayList<>();

    public PomDependenciesHandler(ExternalIdFactory externalIdFactory, boolean includePluginDependencies) {
        this.externalIdFactory = externalIdFactory;
        this.includePluginDependencies = includePluginDependencies;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (isDependencyQName(qName)) {
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
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (isDependencyQName(qName)) {
            parsingDependency = false;

            ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
            dependencies.add(new Dependency(artifact, version, externalId));
        } else {
            parsingNothingImportant();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
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

    private boolean isDependencyQName(String qName) {
        if (includePluginDependencies) {
            return WITH_PLUGINS.contains(qName);
        } else {
            return ONLY_DEPENDENCIES.contains(qName);
        }
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
