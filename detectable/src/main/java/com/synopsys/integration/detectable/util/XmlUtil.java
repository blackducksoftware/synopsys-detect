/**
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
package com.synopsys.integration.detectable.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    public static Node getNode(final String key, final Node parentNode) {
        return getNodeList(key, parentNode).get(0);
    }

    public static List<Node> getNodeList(final String key, final Node parentNode) {
        final List<Node> nodes = new ArrayList<>();
        final NodeList childNodes = parentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(key)) {
                // ignore node generated from DOCTYPE declaration
                if (childNode instanceof DocumentType) {
                    Node nextSibling = childNode.getNextSibling();
                    if (nextSibling.getNodeName().equals(key)) {
                        nodes.add(nextSibling);
                    }
                } else {
                    nodes.add(childNode);
                }
            }
        }
        return nodes;
    }

    public static String getAttribute(final String key, final Node node) {
        return node.getAttributes().getNamedItem(key).getTextContent();
    }

}
