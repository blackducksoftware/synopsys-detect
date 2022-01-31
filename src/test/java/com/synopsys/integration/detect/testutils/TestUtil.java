package com.synopsys.integration.detect.testutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.util.ResourceUtil;

public class TestUtil {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void testJsonResource(String expectedResourcePath, Object object) {
        String expected = getResourceAsUTF8String(expectedResourcePath);
        String actual = gson.toJson(object);
        System.out.println(actual);
        testJson(expected, actual);
    }

    public void testJson(String expectedJson, String actualJson) {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, false);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResourceAsUTF8String(String resourcePath) {
        String data;
        try {
            data = ResourceUtil.getResourceAsString(getClass(), resourcePath, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.stream(data.split("\r?\n")).collect(Collectors.joining(System.lineSeparator()));
    }

    public InputStream getResourceAsInputStream(String resourcePath) {
        return getClass().getResourceAsStream(resourcePath);
    }

    public void createExpectedFile(String resourcePath, Object expectedObject) {
        String expectedJson = gson.toJson(expectedObject);
        File outputFile = new File("src/test/resources", resourcePath);
        outputFile.delete();
        try {
            FileUtils.write(outputFile, expectedJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
