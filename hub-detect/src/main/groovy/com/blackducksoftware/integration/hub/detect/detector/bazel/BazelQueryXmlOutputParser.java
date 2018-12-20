package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class BazelQueryXmlOutputParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final XPathParser xPathParser;

    public BazelQueryXmlOutputParser(final XPathParser xPathParser) {
        this.xPathParser = xPathParser;
    }


    public List<String> parseStringValuesFromRulesConstrained(final String ruleClassName, final String ruleElementSelectorAttrValue) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String xPathQuery = String.format("/query/rule[@class='%s']/%s[@%s='%s']", ruleClassName, "string", "name", ruleElementSelectorAttrValue);
        logger.info(String.format("xPathQuery: %s", xPathQuery));
        List<String> externalIds = xPathParser.parseAttributeValuesWithGivenXPathQuery(xPathQuery, "value");
        return externalIds;
    }

    public List<String> parseStringValuesFromRules(final String ruleClassName, final String ruleElementName, final String ruleElementSelectorAttrName, final String ruleElementSelectorAttrValue, final String ruleElementValueAttrName) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String xPathQuery = String.format("/query/rule[@class='%s']/%s[@%s='%s']", ruleClassName, ruleElementName, ruleElementSelectorAttrName, ruleElementSelectorAttrValue);
        logger.info(String.format("xPathQuery: %s", xPathQuery));
        List<String> externalIds = xPathParser.parseAttributeValuesWithGivenXPathQuery(xPathQuery, ruleElementValueAttrName);
        return externalIds;
    }
}
