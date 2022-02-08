package com.synopsys.integration.detectable.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    public static Node getNode(String key, Node parentNode) {
        return getNodeList(key, parentNode).get(0);
    }

    public static List<Node> getNodeList(String key, Node parentNode) {
        List<Node> nodes = new ArrayList<>();
        NodeList childNodes = parentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
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

    public static String getAttribute(String key, Node node) {
        return node.getAttributes().getNamedItem(key).getTextContent();
    }

}
