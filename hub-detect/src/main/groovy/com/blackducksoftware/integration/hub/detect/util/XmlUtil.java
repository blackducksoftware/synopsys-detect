package com.blackducksoftware.integration.hub.detect.util;

import java.util.ArrayList;
import java.util.List;

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
                nodes.add(childNode);
            }
        }
        return nodes;
    }

    public static String getAttribute(final String key, final Node node) {
        return node.getAttributes().getNamedItem("organisation").getTextContent();
    }

}
