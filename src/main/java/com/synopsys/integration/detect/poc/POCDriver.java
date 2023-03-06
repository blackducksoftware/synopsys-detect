package com.synopsys.integration.detect.poc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synopsys.integration.detect.Application;
import org.json.JSONObject;
import org.json.JSONException;

public class POCDriver {
    private String startDir;
    private POMFinder pomFinder;
    private POMParser pomParser;

    public POCDriver() {
    }

    // btw we are processing strings unsafely -- trusted source assumed
    // 1. Given a maven project directory, find all POMs
    public void drive() {
        try {
            String inputFilePath = "/poc-resources/jsonPayloadDetect.json";
            InputStream inputStream = Application.class.getResourceAsStream(inputFilePath);

            VulnComponentDataset vulnComponentDataset = new VulnComponentDataset();
            String jsonData = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonData);

            // Part 1: Generate vulnerability-component dataset
            JSONObject intermediateResult = vulnComponentDataset.generateVulnComponentDataset(jsonObject);

            System.out.println("\nResult:\n" + intermediateResult.toString(4));

            // Write the intermediate output to a folder
            File targetDir = new File("target/output-files");
            targetDir.mkdirs();
            File outputFile = new File(targetDir, "output.json");
            PrintWriter fileWriter = new PrintWriter(outputFile);
            fileWriter.println(intermediateResult.toString(4));
            fileWriter.close();
        } catch (IOException | JSONException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
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
                pomParser.parsePom(pom, magicDictionary);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return magicDictionary;
    }

}
