package com.synopsys.integration.detect.poc;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VulnComponentDataset {
    private final HashMap<String, MavenDependencyLocation> componentLocationMap;
    private JSONObject resultDataset;

    public VulnComponentDataset(HashMap<String, MavenDependencyLocation> componentLocationMap) {
        this.componentLocationMap = componentLocationMap;
        this.resultDataset = new JSONObject();
    }

    private void addLocationToItem(JSONObject item) throws JSONException {
        MavenDependencyLocation locationObj = this.componentLocationMap.get(item.getString("externalId"));
        if (locationObj == null) {
            locationObj = new MavenDependencyLocation("", 000);
        }
        item.put("filePath", locationObj.getPomFilePath());
        item.put("lineNumber", locationObj.getLineNo());
    }

    private JSONObject generateDirDepObject(JSONObject item) throws JSONException {
        Boolean isExplicitDirect = item.has("transitiveUpgradeGuidance");

        JSONObject newItem = new JSONObject();

        newItem.put("externalId", item.getString("externalId"));
        if (item.has("shortTermUpgradeGuidance")) {
            newItem.put("shortTermUpgradeGuidance", item.getJSONObject("shortTermUpgradeGuidance"));
        }
        if (item.has("longTermUpgradeGuidance")) {
            newItem.put("longTermUpgradeGuidance", item.getJSONObject("longTermUpgradeGuidance"));
        }

        addLocationToItem(newItem);

        if (!isExplicitDirect) {
            newItem.put("affectedTransitiveDependencies", new JSONArray());
        }

        return newItem;
    }

    private void processTransitiveUpgradeGuidance(JSONObject currItem, JSONObject affectedDirDepObject) throws JSONException {
        JSONArray transitiveUpgradeGuidanceArr = currItem.getJSONArray("transitiveUpgradeGuidance");
        String currItemExternalId = currItem.getString("externalId");

        for (int k = 0; k < transitiveUpgradeGuidanceArr.length(); k++) {
            JSONObject currDirDepObj = transitiveUpgradeGuidanceArr.getJSONObject(k);
            String currDirDepObjExtId = currDirDepObj.getString("externalId");

            // fetch or create new
            JSONObject newDirDepObj;
            if (affectedDirDepObject.has(currDirDepObjExtId)) {
                newDirDepObj = affectedDirDepObject.getJSONObject(currDirDepObjExtId);
            } else {
                newDirDepObj = generateDirDepObject(currDirDepObj);
                /*
                 Note: One unhandled edge case here could be if the direct dependency is listed in the components of our source JSON
                 as well as in the "transitiveUpgradeGuidance" section for a transitive dependency *AND* the upgrade guidance in both cases
                 is different. Currently, above code assumes this case doesn't exist and we will see the last modified upgrade guidance.
                 If upgrade guidance is same in such cases, then it doesn't matter.

                 However, if the guidance is different for the same component, to handle it:
                 We need to compare the upgrade guidance sections of both entries and choose the version which is higher.
                 */
            }

            newDirDepObj.getJSONArray("affectedTransitiveDependencies").put(currItemExternalId);;
            affectedDirDepObject.put(currDirDepObjExtId, newDirDepObj);
        }
    }

    private void updateVulnObjectWithDependencies(JSONObject vulnObject, JSONObject currItem) throws  JSONException {
        Boolean isCurrItemDirect = currItem.getJSONArray("transitiveUpgradeGuidance").length() == 0;

        JSONObject affectedDirDepObject = vulnObject.getJSONObject("affectedDirectDependencies");

        String currItemExternalId = currItem.getString("externalId");
        if (isCurrItemDirect && !affectedDirDepObject.has(currItemExternalId)) {
            JSONObject newItemObj = generateDirDepObject(currItem);
            affectedDirDepObject.put(currItemExternalId, newItemObj);
        } else {
            // currItem is transitive
            processTransitiveUpgradeGuidance(currItem, affectedDirDepObject);
        }
    }

    private JSONObject fetchOrCreateNewVulnObject(JSONObject currVulnerability) throws JSONException {
        String currVulnName = currVulnerability.getString("name");
        String currVulnSeverity = currVulnerability.getString("vulnSeverity");
        String currVulnDescription = currVulnerability.getString("description");

        // Fetch the vulnerability object based on its key i.e. vulnName
        JSONObject newVulnObject;
        if (this.resultDataset.getJSONObject(currVulnSeverity).has(currVulnName)) {
            newVulnObject = this.resultDataset.getJSONObject(currVulnSeverity).getJSONObject(currVulnName);
        }
        // If not found, create a new template object
        else {
            newVulnObject = new JSONObject();
            newVulnObject.put("vulnDescription", currVulnDescription);
            newVulnObject.put("affectedDirectDependencies", new JSONObject());
        }
        return newVulnObject;
    }

    public JSONObject generateVulnComponentDataset(JSONObject inputJsonObj) throws JSONException {

        // Create result object template
        this.resultDataset.put("CRITICAL", new JSONObject());
        this.resultDataset.put("HIGH", new JSONObject());
        this.resultDataset.put("MEDIUM", new JSONObject());
        this.resultDataset.put("LOW", new JSONObject());

        JSONArray currItems = inputJsonObj.getJSONArray("items");

        for (int i = 0; i < currItems.length(); i++) {
            JSONObject currItem = currItems.getJSONObject(i);

            // Direct dependency is identified by the transitiveUpgradeGuidance array being empty
            // Assumption based on current source json format: Source JSON will always contain a transitiveUpgradeGuidance field for direct and transitive components.
            // It will be empty for direct, and non-empty for a transitive dependency.
            Boolean isCurrItemDirect = currItem.getJSONArray("transitiveUpgradeGuidance").length() == 0;

            JSONArray vulnerabilities = currItem.getJSONArray("allVulnerabilities");
            for (int j = 0; j < vulnerabilities.length(); j++) {

                JSONObject currVulnerability = vulnerabilities.getJSONObject(j);
                String currVulnName = currVulnerability.getString("name");
                String currVulnSeverity = currVulnerability.getString("vulnSeverity");

                // Fetch or create new vulnerability object
                JSONObject newVulnObject = fetchOrCreateNewVulnObject(currVulnerability);

                // Retrieve affectedDirectDependencies object (may or may not be empty)
                updateVulnObjectWithDependencies(newVulnObject, currItem);

                this.resultDataset.getJSONObject(currVulnSeverity).put(currVulnName, newVulnObject);
            }
        }

        return this.resultDataset;
    }
}

