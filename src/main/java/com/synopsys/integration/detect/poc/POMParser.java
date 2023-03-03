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
    private HashMap<String, MavenDependencyLocation> magicDictionary;
    private HashMap<String, String> propertiesDictionary;


    // 1. Open XML pom file given absolute/relative path
    public HashMap<String, MavenDependencyLocation> parsePom(String filepath) {
        try {
            // given: "src/main/resources/poc-resources/pom.xml"

                        // (directorymanager.getsourcedirectory)
                        // Path sourcePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_SOURCE_PATH);

            // 1. standard way to open file for parsing ...
            File pomFile = new File(filepath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            // w3c library?
            Document xmlDoc = db.parse(pomFile);

            // doc has elements, nodes, tags. attributes.. atrifactId is a NodeName
            NodeList dependencies = xmlDoc.getElementsByTagName("dependency");
            for (int i = 0; i < dependencies.getLength(); i++) { // 110 apparently
                String gav = "";
                Element depElement = (Element) dependencies.item(i);
                NodeList childNodes = depElement.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    //////////
                    // childNodes.item(1) = G
                    // childNodes.item(3) = A
                    // childNodes.item(5) = V
                    // above + .getFirstChild() will give actual text value

                    if (!(childNode instanceof Element)) {
                        continue;
                    }
                    gav += ":";
                    String value = childNode.getFirstChild().getTextContent();
//                    Element childElement = (Element) childNode.getFirstChild();
                    gav += value;
                }
                System.out.println(gav);
            }
            System.out.println("done");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return magicDictionary;
    }

    // 2. Once a dependency is found, store its g:a:v in a hashmap
    private void processDependency() {}

    private void parseProperties() {}
}
