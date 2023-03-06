package com.synopsys.integration.detect.poc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(pomFile);

            int count = 0;
            parseProperties(xmlDoc);
            NodeList dependencies = xmlDoc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencies.getLength(); i++) {
                depElement = (Element) dependencies.item(i);
                childNodes = depElement.getChildNodes();
                ExternalId depExternalId = new ExternalId();
                try {
                    depExternalId.setGroupId(childNodes.item(1).getTextContent());
                    depExternalId.setArtifactId(childNodes.item(3).getTextContent());
                    if (childNodes.item(5).getNodeName() == "version") {
                        String version = childNodes.item(5).getTextContent();
                        // check if version is a variable
                        if (isVariable(version)){
                            MavenProperty propertyValAndLineNo = getVariableValue(version);
                            version = propertyValAndLineNo.getValue();
                        }
                        depExternalId.setVersion(version);
                    } else {
                        System.out.println("\n***EDGE CASE ENCOUNTERED: " + depExternalId.getGAV() + "\n");
                        // TODO print me to a file to collect when edge cases are encountered
                    }
                    System.out.println(count++);
                    // TODO should keep track of keys that got replaced just in case theres something weird going on
                    dictionary.put(depExternalId.getGAV(), new MavenDependencyLocation("pathToPOM", 123));
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
            MavenProperty propertyValAndLineNo = new MavenProperty(propertyValue, 222);
            propertiesDictionary.put(propertyName, propertyValAndLineNo);
        }
//        printPropertiesDictionary(propertiesDictionary);
    }
}
