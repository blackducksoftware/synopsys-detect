package com.synopsys.integration.detectable.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    private XmlUtil() {
        // Hiding constructor
    }

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

    // The least safe way to get the value of an attribute on a node. Can return null and throw exceptions
    @Nullable
    public static String getAttribute(String key, Node node) throws DOMException {
        return getAttributeSafetly(key, node).orElse(null);
    }

    // Slightly safer that getAttribute(key, node). Returns an Optional, but can still throw exceptions
    public static Optional<String> getAttributeSafetly(String key, Node node) throws DOMException {
        return Optional.ofNullable(node.getAttributes().getNamedItem(key))
            .map(Node::getTextContent);
    }

    // The preferred way to get the value of an attribute on a node. Returns Optional with exceptions logged
    public static Optional<String> getAttributeSafetly(String key, Node node, Logger logger) {
        return getAttributeSafetly(key, node, exception -> logger.debug(String.format("Failed to get attribute '%s' from node '%s'", key, node.getNodeName()), exception)
        );
    }

    // If the parser would like to handle the exceptions, but wants the Optional API
    public static Optional<String> getAttributeSafetly(String key, Node node, Consumer<DOMException> exceptionHandler) {
        try {
            return getAttributeSafetly(key, node);
        } catch (DOMException exception) {
            exceptionHandler.accept(exception);
            return Optional.empty();
        }
    }

}
