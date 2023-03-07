package com.synopsys.integration.detect.poc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class POMParser {
    private HashMap<String, MavenProperty> propertiesDictionary = new HashMap<>();


    // 1. Open XML pom file given absolute/relative path
        // note: dep scope doesn't matter right?
    public HashMap<String, MavenDependencyLocation> parsePom(String filepath, HashMap<String, MavenDependencyLocation> dictionary) {
        Element depElement; NodeList childNodes;
        try {
            // given: "src/main/resources/poc-resources/pom.xml"
            // 1. standard way to open file for parsing ...
            File pomFile = new File(filepath);
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = null;
//            db = dbf.newDocumentBuilder();
//            Document xmlDoc = db.parse(pomFile);

            //***********************************************
            //InputSource inputSource = new InputSource(new FileReader(filepath));
            InputStream is = new FileInputStream(pomFile);

            final Document xmlDoc;
            SAXParser parser;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                parser = factory.newSAXParser();
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                xmlDoc = docBuilder.newDocument();
            } catch(ParserConfigurationException e){
                throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
            }
            /////////////////////////////////////////////////
            final Deque<Element> elementStack = new ArrayDeque<>();
            final StringBuilder textBuffer = new StringBuilder();
            DefaultHandler handler = new DefaultHandler() {
                private Locator locator;

                @Override
                public void setDocumentLocator(Locator locator) {
                    this.locator = locator; //Save the locator, so that it can be used later for line tracking when traversing nodes.
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    addTextIfNeeded();
                    Element el = xmlDoc.createElement(qName);
                    for(int i = 0;i < attributes.getLength(); i++)
                        el.setAttribute(attributes.getQName(i), attributes.getValue(i));
                    el.setUserData("lineNo", String.valueOf(locator.getLineNumber()), null);
                    elementStack.push(el);
                }
                @Override
                public void endElement(String uri, String localName, String qName){
                    addTextIfNeeded();
                    Element closedEl = elementStack.pop();
                    if (elementStack.isEmpty()) { // Is this the root element?
                        xmlDoc.appendChild(closedEl);
                    } else {
                        Element parentEl = elementStack.peek();
                        parentEl.appendChild(closedEl);
                    }
                }

                @Override
                public void characters (char ch[], int start, int length) throws SAXException {
                    textBuffer.append(ch, start, length);
                }

                // Outputs text accumulated under the current node
                private void addTextIfNeeded() {
                    if (textBuffer.length() > 0) {
                        Element el = elementStack.peek();
                        Node textNode = xmlDoc.createTextNode(textBuffer.toString());
                        el.appendChild(textNode);
                        textBuffer.delete(0, textBuffer.length());
                    }
                }
            };
            parser.parse(is, handler);
            // look at the xmlDoc
            /////////////////////////////////////////////////

            int count = 0;
            parseProperties(xmlDoc);
            NodeList dependencies = xmlDoc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencies.getLength(); i++) {
                depElement = (Element) dependencies.item(i);
                childNodes = depElement.getChildNodes();

                ExternalId depExternalId = new ExternalId();
                MavenDependencyLocation mdl = new MavenDependencyLocation();

                try {
                    depExternalId.setGroupId(childNodes.item(1).getTextContent());
                    depExternalId.setArtifactId(childNodes.item(3).getTextContent());
                    if (childNodes.item(5).getNodeName() == "version") {
                        String version = childNodes.item(5).getTextContent();
                        // check if version is a variable
                        if (isVariable(version)){
                            MavenProperty propertyValAndLineNo = getVariableValue(version);
                            version = propertyValAndLineNo.getValue();
                            mdl.setLineNo(propertyValAndLineNo.getLineNo());
                        } else {
                            mdl.setLineNo(Integer.valueOf(childNodes.item(5).getUserData("lineNo").toString()));
                        }
                        depExternalId.setVersion(version);
                        mdl.setPomFilePath("pom.xml");
                    } else {
                        System.out.println("\n***EDGE CASE ENCOUNTERED: " + depExternalId.getGAV() + "\n");
                        // TODO print me to a file to collect when edge cases are encountered
                    }
//                    System.out.println(count++);
                    // TODO should keep track of keys that got replaced just in case theres something weird going on
                    dictionary.put(depExternalId.getGAV(), new MavenDependencyLocation(mdl.getPomFilePath(), mdl.getLineNo()));
                } catch (Exception e) {
                    System.out.println("\n***EDGE CASE ENCOUNTERED: " + depExternalId.getGAV() + "\n"); // TODO print me to a file to collect when edge cases are encountered
                    // for example, one edge case is when version is not specified but the build is successful b/c <ignoredUnusedDeclaredDependencies>
                    continue;
                }
            }
            System.out.println("done");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        printDependenciesDictionary(dictionary);
        return dictionary;
    }

    private boolean isVariable(String s) {
        if (s.substring(0, 2).equals("${")) {
            return true;
        }
        return false;
    }
    private MavenProperty getVariableValue(String s) {
        String property = s.substring(2, s.length()-1);
        return propertiesDictionary.get(property);
    }
    private void printDependenciesDictionary(HashMap<String, MavenDependencyLocation> dictionary) {
        System.out.println("\n\t\t\t DEPENDENCIES \n");
        for (String k : dictionary.keySet()){
            MavenDependencyLocation location = dictionary.getOrDefault(k, new MavenDependencyLocation("default", 000));
            System.out.println("\n***" + k + " --> file: " + location.getPomFilePath() + " @ lineNo " + location.getLineNo());
        }
    }

    private void printPropertiesDictionary(HashMap<String, MavenProperty> dictionary) {
        for (String k : dictionary.keySet()){
            MavenProperty property = dictionary.getOrDefault(k, new MavenProperty("default", 000));
            System.out.println("\n***" + k + " --> " + property.getValue() + " file: " + "<same file as the rest>" + " @ lineNo " + property.getLineNo());
        }
    }

    private void parseProperties(Document xmlDoc) {
        NodeList properties = xmlDoc.getElementsByTagName("properties").item(0).getChildNodes();

        for (int i = 0; i < properties.getLength(); i++) {
            Node childNode = properties.item(i);
            // every other child node is an actual property
            if (!(childNode instanceof Element)) {
                continue;
            }
            String propertyName = childNode.getNodeName();
            String propertyValue = childNode.getTextContent();
            int lineNo = Integer.valueOf(childNode.getUserData("lineNo").toString());
            MavenProperty propertyValAndLineNo = new MavenProperty(propertyValue, lineNo);
            propertiesDictionary.put(propertyName, propertyValAndLineNo);
        }
//        printPropertiesDictionary(propertiesDictionary);
    }
}
