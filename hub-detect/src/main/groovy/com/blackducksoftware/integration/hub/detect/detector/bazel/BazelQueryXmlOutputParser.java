/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

    // This method provides a simple interface
    public List<String> parseStringValuesFromRulesConstrained(final String xml, final String ruleClassName, final String ruleElementSelectorAttrValue) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        return parseStringValuesFromRules(xml, ruleClassName, "string", "name", ruleElementSelectorAttrValue, "value");
    }

    // This method provides more flexibility
    public List<String> parseStringValuesFromRules(final String xml, final String ruleClassName, final String ruleElementName, final String ruleElementSelectorAttrName, final String ruleElementSelectorAttrValue, final String ruleElementValueAttrName) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String xPathQuery = String.format("/query/rule[@class='%s']/%s[@%s='%s']", ruleClassName, ruleElementName, ruleElementSelectorAttrName, ruleElementSelectorAttrValue);
        logger.info(String.format("xPathQuery: %s", xPathQuery));
        List<String> externalIds = xPathParser.parseAttributeValuesWithGivenXPathQuery(xml, xPathQuery, ruleElementValueAttrName);
        return externalIds;
    }
}
