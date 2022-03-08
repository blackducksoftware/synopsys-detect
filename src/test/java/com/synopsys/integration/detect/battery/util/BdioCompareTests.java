package com.synopsys.integration.detect.battery.util;

import static com.synopsys.integration.detect.battery.util.BatteryFiles.UTIL_RESOURCE_PREFIX;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONParser;

public class BdioCompareTests {
    @Test
    public void actualMissingTwoNodes() throws IOException, JSONException, BdioCompare.BdioCompareException {
        //two-root has two nodes from the root, so when compared to empty bdio we should get 4 differences of the 2 missing nodes and 2 missing relationships.
        JSONArray expected = loadBdio("two-root-bdio");
        JSONArray actual = loadBdio("empty-bdio");
        Assertions.assertEquals(4, new BdioCompare().compare(expected, actual).size());
    }

    @Test
    public void actualMissingRelationship() throws IOException, JSONException, BdioCompare.BdioCompareException {
        //two-root-related has a relationship between node1 and node2 that two-root does not, so we should get a difference of 1.
        JSONArray expected = loadBdio("two-root-related-bdio");
        JSONArray actual = loadBdio("two-root-bdio");
        Assertions.assertEquals(1, new BdioCompare().compare(expected, actual).size());
    }

    @Test
    public void identicalMatches() throws IOException, JSONException, BdioCompare.BdioCompareException {
        JSONArray expected = loadBdio("two-root-bdio");
        JSONArray actual = loadBdio("two-root-bdio");
        Assertions.assertEquals(0, new BdioCompare().compare(expected, actual).size());
    }

    private JSONArray loadBdio(String name) throws JSONException, IOException {
        File file = BatteryFiles.asFile("/bdio-compare/" + name + ".jsonld", UTIL_RESOURCE_PREFIX);
        String text = FileUtils.readFileToString(file, Charset.defaultCharset());
        return (JSONArray) JSONParser.parseJSON(text);
    }
}


