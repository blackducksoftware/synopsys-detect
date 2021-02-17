/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
