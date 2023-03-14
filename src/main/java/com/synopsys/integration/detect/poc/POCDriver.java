package com.synopsys.integration.detect.poc;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class POCDriver {
    private POMFinder pomFinder;
    private POMParser pomParser;

    public POCDriver() {
    }

    // btw we are processing strings unsafely -- trusted source assumed
    // 1. Given a maven project directory, find all POMs
    public void drive(File fullResultJsonFile, DirectoryManager directoryManager) {
        try {
            InputStream inputStream = new FileInputStream(fullResultJsonFile);
//            String inputFilePath = "/poc-resources/jsonPayloadDetect.json";
//            String inputFilePath = jsonFilePath;
//            InputStream inputStream = Application.class.getResourceAsStream(inputFilePath);

            String jsonData = new String(inputStream.readAllBytes());
            JSONArray sourceJsonArray = new JSONArray(jsonData);
            JSONObject sourceJsonObject = new JSONObject();
            sourceJsonObject.put("items", sourceJsonArray);

            // Part A: Generate component-location hash map
            HashMap<String, MavenDependencyLocation> componentLocationMap = giveMeDictionary(directoryManager);

            // Part B: Generate vulnerability-component dataset
            VulnComponentDataset vulnComponentDataset = new VulnComponentDataset(componentLocationMap);
//            VulnComponentDataset vulnComponentDataset = new VulnComponentDataset();
            JSONObject result = vulnComponentDataset.generateVulnComponentDataset(sourceJsonObject);

            System.out.println("\nResult:\n" + result.toString(4));

            // Write the intermediate output to a folder
            File targetDir = new File("target/output-files");
            targetDir.mkdirs();
            File outputFile = new File(targetDir, "vulnerability-component-dataset.json");
            PrintWriter fileWriter = new PrintWriter(outputFile);
            fileWriter.println(result.toString(4));
            fileWriter.close();
        } catch (IOException | JSONException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public HashMap<String, MavenDependencyLocation> giveMeDictionary(DirectoryManager dm) {
        // --detect.source.path=/Users/shanty/blackduck/gitlab-folder/poc-source/triage
        // ***************************** //
        // path from repository root
//        startDir = "src/main/resources/poc-resources/pom.xml";
        File startDir = dm.getSourceDirectory();
        // ***************************** //
        pomFinder = new POMFinder();
        List<String> pomPaths = pomFinder.findAllProjectPOMs(startDir);

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
