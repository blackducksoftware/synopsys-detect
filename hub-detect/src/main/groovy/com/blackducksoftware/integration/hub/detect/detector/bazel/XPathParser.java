package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String xmlString;

    public XPathParser(final String xmlString) {
        this.xmlString = xmlString;
    }

    public void parse(final String xPathExpression, final String targetAttributeName) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        InputStream xmlInputStream = new ByteArrayInputStream(xmlString.getBytes());

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(xmlInputStream);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(xPathExpression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i=0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            logger.info(String.format("External ID: %s", node.getAttributes().getNamedItem(targetAttributeName)));
        }
    }
}
