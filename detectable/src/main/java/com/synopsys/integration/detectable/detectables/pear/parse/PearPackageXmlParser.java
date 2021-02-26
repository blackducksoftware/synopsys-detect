/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pear.parse;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.synopsys.integration.detectable.util.XmlUtil;
import com.synopsys.integration.util.NameVersion;

public class PearPackageXmlParser {
    public NameVersion parse(final InputStream packageXmlInputStream)
        throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final NameVersion nameVersion = new NameVersion();
        final Document packageXml = builder.parse(packageXmlInputStream);
        final Node packageNode = XmlUtil.getNode("package", packageXml);
        nameVersion.setName(XmlUtil.getNode("name", packageNode).getTextContent());
        final Node versionNode = XmlUtil.getNode("version", packageNode);
        nameVersion.setVersion(XmlUtil.getNode("release", versionNode).getTextContent());

        return nameVersion;
    }
}
