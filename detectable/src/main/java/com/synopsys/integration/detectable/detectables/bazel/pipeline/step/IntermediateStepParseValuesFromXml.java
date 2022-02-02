package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class IntermediateStepParseValuesFromXml implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String xPathToElement;
    private final String targetAttributeName;

    public IntermediateStepParseValuesFromXml(String xPathToElement, String targetAttributeName) {
        this.xPathToElement = xPathToElement;
        this.targetAttributeName = targetAttributeName;
    }

    @Override
    public List<String> process(List<String> input) throws DetectableException {
        List<String> results = new ArrayList<>();
        for (String xmlDoc : input) {
            List<String> values;
            try {
                values = parseAttributeValuesWithGivenXPathQuery(xmlDoc, xPathToElement, targetAttributeName);
            } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
                String msg = String.format("Error parsing xml %s for xPath query %s, attribute name: %s", xmlDoc, xPathToElement, targetAttributeName);
                logger.debug(msg);
                throw new DetectableException(msg, e);
            }
            results.addAll(values);
        }
        return results;
    }

    private List<String> parseAttributeValuesWithGivenXPathQuery(String xmlString, String xPathExpression, String targetAttributeName)
        throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        logger.trace("xPathExpression: {}, targetAttributeName: {}", xPathExpression, targetAttributeName);
        List<String> parsedValues = new ArrayList<>();
        InputStream xmlInputStream = new ByteArrayInputStream(xmlString.getBytes());

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(xmlInputStream);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(xPathExpression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            logger.trace("parsed value: {}", node.getAttributes().getNamedItem(targetAttributeName).getTextContent());
            parsedValues.add(node.getAttributes().getNamedItem(targetAttributeName).getTextContent());
        }
        return parsedValues;
    }
}
