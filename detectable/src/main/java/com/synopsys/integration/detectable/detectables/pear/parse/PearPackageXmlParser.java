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
    public NameVersion parse(InputStream packageXmlInputStream)
        throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = factory.newDocumentBuilder();

        NameVersion nameVersion = new NameVersion();
        Document packageXml = builder.parse(packageXmlInputStream);
        Node packageNode = XmlUtil.getNode("package", packageXml);
        nameVersion.setName(XmlUtil.getNode("name", packageNode).getTextContent());
        Node versionNode = XmlUtil.getNode("version", packageNode);
        nameVersion.setVersion(XmlUtil.getNode("release", versionNode).getTextContent());

        return nameVersion;
    }
}
