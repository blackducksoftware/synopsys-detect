package com.synopsys.integration.detectable.detectables.ivy.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IvyProjectNameSaxHandler extends DefaultHandler {
    private static final String PROJECT = "project";
    private static final String NAME = "name";

    private String projectName;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals(PROJECT)) {
            String projectName = attributes.getValue(NAME);
            if (projectName != null) {
                this.projectName = projectName;
            }
        }
    }

    public String getProjectName() {
        return projectName;
    }

}
