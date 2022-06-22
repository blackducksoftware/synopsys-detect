package com.synopsys.integration.detectable.detectables.ivy.parse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class IvyDependenciesSaxHandler extends DefaultHandler {
    private static final String DEPENDENCY = "dependency";
    private static final String ORG_KEY = "org";
    private static final String NAME_KEY = "name";
    private static final String REV_KEY = "rev";

    private final ExternalIdFactory externalIdFactory;

    private final List<Dependency> dependencies = new ArrayList<>();

    private String org;
    private String name;
    private String rev;

    public IvyDependenciesSaxHandler() {
        this.externalIdFactory = ExternalId.FACTORY;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals(DEPENDENCY)) {
            org = parseAttribute(attributes, ORG_KEY);
            name = parseAttribute(attributes, NAME_KEY);
            rev = parseAttribute(attributes, REV_KEY);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals(DEPENDENCY)) {
            ExternalId externalId = externalIdFactory.createMavenExternalId(org, name, rev);
            dependencies.add(new Dependency(name, rev, externalId));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    private String parseAttribute(Attributes attributes, String attribute) {
        return attributes.getValue(attribute);
    }
}
