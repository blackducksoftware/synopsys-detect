package com.synopsys.integration.detect.poc;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class POCDriver {
    private String startDir;
    private POMFinder pomFinder;
    private POMParser pomParser;

    public POCDriver() {
    }

    // btw we are processing strings unsafely -- trusted source assumed
    // 1. Given a maven project directory, find all POMs
    public void drive() {
        // <insert Nirav's main function>

    }

    private HashMap<String, MavenDependencyLocation> giveMeDictionary() {
        pomFinder = new POMFinder();
        List<String> pomPaths = pomFinder.findAllProjectPOMs();

        // ***************************** //
        // path from repository root
        startDir = "src/main/resources/poc-resources/pom.xml";
        pomPaths.add(startDir);
        // ***************************** //

        HashMap<String, MavenDependencyLocation> magicDictionary = new HashMap<>();
        pomParser = new POMParser();
        for (String pom : pomPaths) {
            try {
                pomParser.parsePom(pom);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return magicDictionary;
    }

}
